
# Building Lilo Android

Après clôné le repository, ouvrir le répertoire `./mobile/android/fenix/` sous Android Studio.

```
# Clone the repository
git clone https://github.com/liloteam/android-app.git --recursive
cd android-app

# If you forgot the --recursive flag when cloning:
git submodule update --init
```

Version minimale pour Android Studio : Meerkat Feature Drop | 2024.3.2

Dans un premier temps, vous devriez voir le message suivant après la résolution des fichier Gradle : `Could not run ./mach environment. Try running ./mach build first`.

## Paramétrage de l'environnement

### Bootstrap process

Lilo Android utilise le système de démarrage de Mozilla qui télécharge automatiquement les composants de la chaîne d'outils.

 - Le système bootstrap s'occupe du téléchargement des compilateurs et des outils
 - Pour les versions de développement, il télécharge tout automatiquement
 - Pour les versions de production, certains composants doivent être installés manuellement

Dans le répertoire racine du dépôt, exécuter la commande :
```
python3 bootstrap.py
./mach --no-interactive bootstrap --application-choice="GeckoView/Firefox for Android Artifact Mode"
```

### Erreurs possibles

Si vous n'avez pas exécuté la commande `mach bootstrap` avant `mach build` alors vous aurez l'erreur suivante dans Android Studio :
```
Caused by: org.gradle.api.GradleException: Building with Gradle is only supported for Firefox for Android, i.e., MOZ_BUILD_APP == 'mobile/android'.
```

Si vous n'avez pas exécuté la commande `mach build` avant d'ouvrir le projet, alors vous aurez l'erreur suivante dans Android Studio :
`Caused by: org.gradle.api.GradleException: Could not run ./mach environment. Try running ./mach build first`.