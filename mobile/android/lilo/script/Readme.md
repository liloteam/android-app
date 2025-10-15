# Android Language File Word Replacer

This Python script automates the replacement of a specific word in Android Studio language files (`strings.xml`), preserving the original casing and ensuring only full word matches are replaced. It also preserves XML comments.

## Features

- ✅ Replaces full word matches only (e.g. "bonjour" → "salut", not "bonjours")
- ✅ Preserves original casing (e.g. "Bonjour" → "Salut", "BONJOUR" → "SALUT")
- ✅ Preserves XML comments (e.g. `<!-- Translator note -->`)
- ✅ Processes multiple language files based on language codes
- ✅ Reports number of replacements per file
- ✅ Warns about missing or unreadable files

## Requirements

- Python 3.x
- `lxml` library

### Installation

To install the required dependency, run:

```bash
pip3 install lxml

### Example of use

python3 replace-word.py Firefox Lilo "en,fr" ../../fenix/app/src/main/res
