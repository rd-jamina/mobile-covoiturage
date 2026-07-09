# app_covoiturage

Application mobile de covoiturage développée en Kotlin (Jetpack Compose) pour Android, connectant chauffeurs et passagers pour Madagascar.

---

## 1. Structure du projet

L'application suit une architecture **Clean Architecture** en 3 couches, combinée au pattern **MVVM** (Model-View-ViewModel), avec injection de dépendances via **Hilt**.

```
com.example.app_covoiturage/
│
├── App.kt                          # Classe Application (@HiltAndroidApp)
├── MainActivity.kt                 # Point d'entrée, lance AppNavHost
│
├── data/
│   ├── local/                      # (réservé Room, cache offline)
│   ├── remote/
│   │   ├── osrm/                   # Calcul d'itinéraires routiers (OSRM)
│   │   └── nominatim/               # Géocodage inversé (coordonnées → nom de lieu)
│   ├── repository/                 # Implémentations concrètes des repositories
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── TripRepositoryImpl.kt
│   │   ├── VehicleRepositoryImpl.kt
│   │   ├── ReservationRepositoryImpl.kt
│   │   ├── RouteRepositoryImpl.kt
│   │   └── GeocodingRepositoryImpl.kt
│   └── mapper/
│
├── domain/
│   ├── model/                      # Modèles métier (User, Trip, Vehicle, Reservation...)
│   ├── repository/                 # Interfaces des repositories (contrats)
│   └── usecase/                    # Logique métier, un fichier = une action
│       ├── auth/                   # LoginUseCase, RegisterUseCase
│       ├── driver/                 # PublishTripUseCase, GetDriverReservationsUseCase...
│       ├── passenger/               # SearchTripsUseCase, BookTripUseCase...
│       ├── profile/
│       └── map/                    # GetRouteUseCase, GetPlaceNameUseCase
│
├── presentation/
│   ├── onboarding/                 # Écran d'accueil (Pager)
│   ├── auth/
│   │   ├── login/
│   │   └── register/               # Inscription + choix du rôle (Chauffeur/Passager)
│   ├── driver/
│   │   ├── setup/                  # Infos perso, véhicule, documents (inscription)
│   │   ├── dashboard/
│   │   ├── trip/
│   │   │   ├── publish/            # Publier un trajet
│   │   │   └── history/            # Historique des trajets
│   │   ├── reservations/           # Réservations reçues + détail + Accepter/Refuser
│   │   ├── profile/                # Profil + modification infos/véhicule
│   │   └── vehicle/                # Liste et formulaire multi-véhicules
│   ├── passenger/
│   │   ├── setup/                  # Infos personnelles (inscription)
│   │   ├── dashboard/
│   │   ├── trip/
│   │   │   ├── search/             # Rechercher un trajet
│   │   │   ├── results/            # Résultats de recherche
│   │   │   └── detail/             # Info détaillé + Réserver
│   │   ├── payment/                 # Paiement (simulation)
│   │   ├── history/                 # Mes réservations
│   │   ├── profile/                 # Profil + modification infos
│   │   └── preferences/             # Préférences de voyage
│   ├── common/
│   │   └── notifications/           # Écran de notifications, commun aux 2 rôles
│   ├── map/                         # Sélection de lieu sur carte + affichage trajet
│   ├── navigation/                  # AppNavHost + Routes (Jetpack Navigation Compose)
│   └── ui.theme/                    # Thème Material 3
│
└── di/                              # Modules Hilt (Supabase, Repository, OSRM, Nominatim)
```

---

## 2. Fonctionnement

### 2.1 Parcours utilisateur

```
Onboarding
   ↓
Login  ⇄  Register (avec choix du rôle Chauffeur/Passager)
   ↓
┌──────────────────────┐        ┌──────────────────────────┐
│   Côté CHAUFFEUR      │        │    Côté PASSAGER          │
├──────────────────────┤        ├──────────────────────────┤
│ Setup (infos, véhicule,│        │ Setup (infos personnelles)│
│ documents)             │        │                            │
│        ↓               │        │        ↓                   │
│  Tableau de bord        │        │   Tableau de bord           │
│  ├─ Publier un trajet   │        │   ├─ Rechercher un trajet   │
│  ├─ Réservations reçues │        │   │      ↓                  │
│  │    ├─ Info détaillé  │        │   │  Résultats              │
│  │    └─ Accepter/Refuser│       │   │      ↓                  │
│  ├─ Historique trajets  │        │   │  Info détaillé          │
│  ├─ Notifications       │        │   │      ↓                  │
│  └─ Profil / Véhicules  │        │   │   Réserver               │
│                          │        │   │      ↓                  │
│                          │        │   │   Paiement               │
│                          │        │   ├─ Mes réservations       │
│                          │        │   ├─ Notifications          │
│                          │        │   └─ Profil / Préférences   │
└──────────────────────┘        └──────────────────────────┘
```

### 2.2 Flow métier principal (bout en bout)

1. Le **chauffeur** publie un trajet (origine, destination, date, places, prix), avec sélection des points sur une carte OpenStreetMap.
2. Le **passager** recherche un trajet selon les mêmes critères (origine, destination, date).
3. Le passager consulte les détails d'un résultat et **réserve** un nombre de places → une ligne est créée dans `reservations`.
4. Le passager passe à l'écran **Paiement** (simulation, sans transaction réelle) → `payment_status` passe à `PAID`.
5. Le **chauffeur** reçoit la demande dans "Réservations reçues" et peut **Accepter** ou **Refuser**.
6. Des **notifications** sont générées automatiquement (triggers SQL) à chaque étape clé : nouvelle réservation, réponse du chauffeur, confirmation de paiement.

### 2.3 Cartographie et itinéraires

- Sélection de lieux sur une carte **OpenStreetMap** (via osmdroid), restreinte à la zone de Madagascar.
- **Géocodage inversé** (coordonnées → nom de lieu réel) via l'API **Nominatim**.
- **Calcul d'itinéraire routier réel** (pas une ligne droite) via **OSRM** (Open Source Routing Machine).
- Ces trois services sont gratuits et ne nécessitent aucune clé API ni compte de facturation, contrairement à Google Maps Platform.

---

## 3. Outils et Base de données

### 3.1 Stack technique

| Catégorie | Outil |
|---|---|
| Langage | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | Clean Architecture + MVVM |
| Injection de dépendances | Hilt (Dagger) |
| Navigation | Jetpack Navigation Compose |
| Backend / Base de données | Supabase (PostgreSQL) |
| Authentification | Supabase Auth |
| Stockage de fichiers | Supabase Storage (photos de profil, documents) |
| Appels réseau | Retrofit + OkHttp + Gson |
| Sérialisation | kotlinx.serialization |
| Cartographie | osmdroid (OpenStreetMap) |
| Géocodage inversé | Nominatim (OpenStreetMap) |
| Calcul d'itinéraire | OSRM (Open Source Routing Machine) |
| Chargement d'images | Coil |
| Notifications push (prévu) | Firebase Cloud Messaging |

### 3.2 Base de données — tables PostgreSQL (Supabase)

| Table | Rôle |
|---|---|
| `profiles` | Profil utilisateur étendu (nom, téléphone, genre, date de naissance, photo, rôle actif) |
| `vehicles` | Véhicules enregistrés par les chauffeurs (marque, modèle, plaque, places) — un chauffeur peut avoir plusieurs véhicules |
| `trips` | Trajets publiés (origine, destination, coordonnées GPS, date, places, prix, statut) |
| `reservations` | Réservations passager sur un trajet (places réservées, statut, statut de paiement) |
| `travel_preferences` | Préférences de voyage du passager (musique, animaux, discussion...) |
| `notifications` | Notifications générées automatiquement pour les 2 rôles |

### 3.3 Sécurité

- **Row Level Security (RLS)** activée sur toutes les tables : chaque utilisateur ne peut lire/modifier que ses propres données.
- Des **triggers SQL** automatisent certaines actions :
  - Création automatique d'un `profile` à l'inscription (`handle_new_user`)
  - Notification au chauffeur lors d'une nouvelle réservation
  - Notification au passager lors d'une réponse du chauffeur (accepté/refusé)
  - Notification au passager lors de la confirmation de paiement

### 3.4 Configuration requise

Le projet nécessite un fichier `local.properties` (non versionné) contenant :
```properties
SUPABASE_URL=...
SUPABASE_ANON_KEY=...
```

Aucune clé Google Maps n'est nécessaire (remplacée par osmdroid/OSRM/Nominatim, gratuits et sans facturation).

---

## 4. État d'avancement

| Module | Écrans fonctionnels |
|---|---|
| Authentification | 3/3 |
| Chauffeur | 13/13 (upload documents partiellement finalisé) |
| Passager | 12/14 |
| Cartographie | Fonctionnelle (sélection, géocodage, itinéraire) |

**Reste à faire :** upload réel des documents chauffeur vers Supabase Storage, notifications push (FCM), vérification de session persistante au démarrage, sécurisation finale des règles RLS avant mise en production.
