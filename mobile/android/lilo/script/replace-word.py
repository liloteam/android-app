"""
replace_word.py

Description:
    This script replaces a specific word with another in Android Studio language files (strings.xml),
    preserving the original casing (e.g. "bonjour" → "salut", "Bonjour" → "Salut", "BONJOUR" → "SALUT").
    Only complete word matches are replaced, and only in user-visible strings (<string> and <item> tags).

Usage:
    python replace_word.py <word_to_replace> <replacement_word> <lang_codes> <res_dir>

Arguments:
    <word_to_replace>     The word to search for (case-insensitive, whole word only)
    <replacement_word>    The word to replace it with
    <lang_codes>          Comma-separated list of language codes (e.g. "en,fr,en-rGB")
    <res_dir>             Relative path to the Android 'res/' directory (e.g. "./app/src/main/res")

Example:
    python replace_word.py bonjour salut "en,fr,en-rGB" "./app/src/main/res"

Output:
    - Number of replacements per file
    - Warning for missing language files
    - Error messages for unreadable or malformed XML files

Author:
    François (iAdaM)
"""

from lxml import etree
import sys
import os
import re

def get_language_paths(res_dir, lang_codes):
    paths = {}
    for code in lang_codes.split(','):
        code = code.strip()
        folder = "values" if code == "en" else f"values-{code}"
        path = os.path.join(res_dir, folder, "strings.xml")
        paths[code] = path
    return paths

def match_case(original, replacement):
    if original.isupper():
        return replacement.upper()
    elif original.islower():
        return replacement.lower()
    elif original[0].isupper():
        return replacement.capitalize()
    else:
        return replacement

def replace_word_preserving_case(text, target, replacement):
    count = 0
    def replacer(match):
        nonlocal count
        count += 1
        return match_case(match.group(), replacement)
    pattern = r'\b' + re.escape(target) + r'\b'
    new_text = re.sub(pattern, replacer, text)
    return new_text, count

def process_files(word_to_replace, replacement_word, lang_codes, res_dir):
    paths = get_language_paths(res_dir, lang_codes)
    parser = etree.XMLParser(remove_blank_text=False, remove_comments=False)

    for code, file_path in paths.items():
        if not os.path.exists(file_path):
            print(f"❌ Missing file for language '{code}': {file_path}")
            continue

        try:
            tree = etree.parse(file_path, parser)
            root = tree.getroot()
            modified = False
            file_count = 0

            for element in root.iter():
                if isinstance(element.tag, str) and element.tag in ['string', 'item'] and element.text:
                    new_text, count = replace_word_preserving_case(element.text, word_to_replace, replacement_word)
                    if count > 0:
                        element.text = new_text
                        file_count += count
                        modified = True

            if modified:
                tree.write(file_path, encoding='utf-8', xml_declaration=True, pretty_print=True)
                print(f"✅ {file_path}: {file_count} replacements")
            else:
                print(f"ℹ️ {file_path}: no replacements")

        except Exception as e:
            print(f"❌ Error processing {file_path}: {e}")

if __name__ == "__main__":
    if len(sys.argv) < 5:
        print("Usage: python replace_word.py <word_to_replace> <replacement_word> <lang_codes> <res_dir>")
        print("Example: python replace_word.py bonjour salut \"en,fr,en-rGB\" \"./app/src/main/res\"")
        sys.exit(1)

    word_to_replace = sys.argv[1]
    replacement_word = sys.argv[2]
    lang_codes = sys.argv[3]
    res_dir = sys.argv[4]

    process_files(word_to_replace, replacement_word, lang_codes, res_dir)