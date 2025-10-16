# Bookmark API Integration

Ce package contient la logique pour récupérer les favoris depuis l'API Lilo distante en utilisant l'architecture Mozilla Components.

## Structure

- **BookmarkModel.kt** : Modèle de données représentant un favori
- **BookmarkApiService.kt** : Service pour effectuer l'appel API avec `mozilla.components.concept.fetch.Client`
- **BookmarkRetrieverExample.kt** : Exemple d'utilisation

## Architecture

Cette implémentation utilise **`mozilla.components.concept.fetch.Client`**, qui est le pattern standard dans Fenix pour effectuer des requêtes HTTP. Cela offre plusieurs avantages :

- ✅ Cohérence avec l'architecture Fenix
- ✅ Gestion automatique du cache via Gecko
- ✅ Support natif des timeouts et options avancées
- ✅ Facilement testable avec mocking
- ✅ Utilisation de `Result.runCatching` pour la gestion d'erreurs

## Utilisation

### Récupération des favoris depuis votre application

```kotlin
// Obtenir le client depuis les composants Core de Fenix
val client = (application as FenixApplication).components.core.client

// Créer le MigrationManager avec le client
val migrationManager = MigrationManager(applicationContext, client)

// Récupérer les favoris
migrationManager.fetchRemoteBookmarks("your-user-token-here")
```

### Pour les tests unitaires

```kotlin
// Créer un client de test
val testClient = GeckoViewFetchClient(context, geckoRuntime)

// Ou utiliser un mock
val mockClient = mock<Client>()
whenever(mockClient.fetch(any())).thenReturn(mockResponse)

val migrationManager = MigrationManager(applicationContext, testClient)
migrationManager.fetchRemoteBookmarks("test-token")
```

### API Endpoint

L'API utilisée est : `https://ws.lilo.org/users/{token}/bookmarks`

### Format de réponse

```json
[
  {
    "id": "03da2f02-8b8c-4f5d-b2af-7221e2b77a31",
    "color": "#2c8807",
    "title": "Etherscan",
    "url": "https://etherscan.io/gastracker",
    "icon": "https://ws.lilo.org/uploads/favicons/6a04a374d549abc7a9c36f67baccad50.ico"
  },
  {
    "id": "2577ba7c-d32b-4f01-96bd-01da1715779e",
    "color": "#036dc0",
    "title": "Radiofrance",
    "url": "https://www.radiofrance.fr/franceinter",
    "icon": "https://ws.lilo.org/uploads/favicons/b7bee9d8440c8ffadacafa90103c49d3.ico"
  }
]
```

## Logs Logcat

Les logs sont visibles dans Logcat avec les tags suivants :

- **LILO:LOG:MIGRATION** : Logs du MigrationManager
- **LILO:API:BOOKMARKS** : Logs détaillés du service API

### Exemple de logs

```
D/LILO:LOG:MIGRATION: Fetching remote bookmarks for token: xxxxx
D/LILO:API:BOOKMARKS: Fetching bookmarks from: https://ws.lilo.org/users/xxxxx/bookmarks
D/LILO:API:BOOKMARKS: Response received: [{"id":"...","color":"...","title":"...","url":"...","icon":"..."}]
D/LILO:API:BOOKMARKS: Bookmark #1:
D/LILO:API:BOOKMARKS:   ID: 03da2f02-8b8c-4f5d-b2af-7221e2b77a31
D/LILO:API:BOOKMARKS:   Title: Etherscan
D/LILO:API:BOOKMARKS:   URL: https://etherscan.io/gastracker
D/LILO:API:BOOKMARKS:   Color: #2c8807
D/LILO:API:BOOKMARKS:   Icon: https://ws.lilo.org/uploads/favicons/...
D/LILO:API:BOOKMARKS: Successfully fetched 2 bookmarks
I/LILO:LOG:MIGRATION: Retrieved 2 bookmarks from API:
```

## Gestion des erreurs

Le service gère automatiquement les erreurs suivantes :
- Erreurs de connexion réseau
- Réponses HTTP non-OK (via `response.isSuccess`)
- Erreurs de parsing JSON
- Timeouts (gérés par le Client)

Toutes les erreurs sont loggées dans Logcat avec le niveau ERROR et la méthode retourne une liste vide en cas d'erreur.

## Dépendances

Le module `liloapp.migration` utilise déjà les dépendances nécessaires :

```gradle
implementation project(':components:concept-fetch')
implementation project(':components:browser-engine-gecko')
```

Le `Client` est typiquement fourni par `GeckoViewFetchClient` qui est disponible via les composants Core de l'application.

## Exemple complet dans le contexte Fenix

```kotlin
class MyActivity : AppCompatActivity() {
    
    private lateinit var migrationManager: MigrationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Obtenir le client depuis les composants
        val components = (application as FenixApplication).components
        val client = components.core.client
        
        // Initialiser le MigrationManager
        migrationManager = MigrationManager(applicationContext, client)
        
        // Récupérer les favoris d'un utilisateur
        val userToken = "user-token-from-auth"
        migrationManager.fetchRemoteBookmarks(userToken)
    }
}
```
