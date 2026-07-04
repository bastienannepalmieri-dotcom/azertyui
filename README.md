# Trading Block — Mod Minecraft Forge 1.19.2

Ajoute un bloc **"Bloc d'Échange"** qui permet d'acheter **n'importe quel item
du jeu** (vanilla + tous les mods de ton modpack) au tarif fixe :

> **1 émeraude = 64 exemplaires de l'item choisi**

La liste des items n'est PAS codée en dur dans le mod : elle est lue
**automatiquement** depuis le registre du jeu au moment où tu ouvres
l'interface.

## Comment ça marche en jeu

1. Obtiens le bloc `Bloc d'Échange` (`/give @p tradingblock:trading_block`
   en créatif, ou cherche-le dans JEI).
2. Pose-le, clique-droit dessus : une interface s'ouvre.
3. Dépose une ou plusieurs émeraudes dans le slot de gauche.
4. Cherche l'item que tu veux avec la barre de recherche, navigue avec
   les flèches `<` `>`.
5. Clique sur l'item : ça consomme 1 émeraude et te donne 64 exemplaires.

---

## 🌐 Compiler le .jar SANS RIEN INSTALLER (méthode recommandée)

Comme je n'ai pas accès aux serveurs de Forge depuis mon environnement, je
ne peux pas te fournir le `.jar` directement. Mais j'ai préparé ce projet
pour qu'il se compile **tout seul sur les serveurs de GitHub**, gratuitement.
Tu n'as besoin que d'un navigateur et d'un compte GitHub (gratuit).

### Étape 1 — Crée un compte GitHub (si tu n'en as pas)
Va sur https://github.com/join et crée un compte gratuit.

### Étape 2 — Crée un nouveau dépôt (repository)
1. Clique sur le **+** en haut à droite → **New repository**
2. Donne-lui un nom, par exemple `tradingblock-mod`
3. Laisse-le en **Public** (ou Private, peu importe)
4. Ne coche aucune case (pas de README, pas de .gitignore)
5. Clique **Create repository**

### Étape 3 — Envoie les fichiers du mod
1. Extrais le zip que je t'ai donné (`tradingblock-mod-source.zip`) sur ton
   PC — tu dois obtenir un dossier `tradingblock` contenant `src`,
   `.github`, `build.gradle`, etc.
2. Sur la page de ton nouveau dépôt GitHub, clique **"uploading an existing
   file"** (ou **Add file → Upload files**)
3. Ouvre le dossier `tradingblock` extrait, sélectionne **tout son contenu**
   (Ctrl+A à l'intérieur du dossier, PAS le dossier `tradingblock`
   lui-même) et fais un **glisser-déposer** dans la zone d'upload de GitHub.
   Chrome/Edge conservent l'arborescence des sous-dossiers automatiquement.
4. En bas de page, clique **Commit changes**

### Étape 4 — Laisse GitHub compiler automatiquement
1. Va dans l'onglet **Actions** en haut de ton dépôt.
2. Tu devrais voir un workflow **"Build Trading Block Mod"** en train de
   tourner (rond jaune/orange qui tourne). Clique dessus pour suivre la
   progression en direct.
3. Patiente 3 à 8 minutes (Gradle télécharge Forge et compile).
4. Si tu vois une coche verte ✅ → c'est réussi !
   Si tu vois une croix rouge ❌ → clique dessus, copie-moi le message
   d'erreur affiché dans les logs et je corrigerai le code.

### Étape 5 — Télécharge le .jar compilé
1. Toujours sur la page du run terminé (coche verte), descends en bas de
   page jusqu'à la section **Artifacts**.
2. Clique sur **tradingblock-jar** pour télécharger un zip.
3. Décompresse ce zip : à l'intérieur se trouve le vrai fichier
   `tradingblock-1.0.0.jar`.
4. Copie ce `.jar` dans le dossier `mods` de ton modpack, à côté de tes
   300 autres mods.

C'est tout — aucune installation locale, tout s'est passé dans le
navigateur et sur les serveurs de GitHub.

---

## Méthode alternative — compiler en local (si tu préfères)

Si tu as déjà ou veux installer un JDK 17 + le Forge MDK sur ton PC, voir
la section détaillée plus bas *(gardée pour référence, moins pratique que
la méthode GitHub ci-dessus)*.

### Étape 1 — JDK 17
https://adoptium.net/temurin/releases/?version=17 → Windows x64 JDK → installer.

### Étape 2 — Forge MDK
https://files.minecraftforge.net/net/minecraftforge/forge/index_1.19.2.html
→ version 1.19.2-43.3.0 → télécharger **Mdk** → extraire.

### Étape 3 — Remplacer les fichiers
Supprime le dossier `example` dans `src/main/java/com/`, remplace
`src/main/java/com/bastien`, `src/main/resources/assets/tradingblock`,
`src/main/resources/data/tradingblock`, `META-INF/mods.toml` et
`pack.mcmeta` par les miens.

### Étape 4 — Compiler
Terminal dans le dossier du MDK :
```
gradlew build
```

### Étape 5 — Récupérer le jar
`build/libs/tradingblock-1.0.0.jar` → dossier `mods`.

---

## Si la compilation échoue (GitHub ou locale)

- **Erreur sur `new Button(...)`** : remplace dans `TradingScreen.java` :
  ```java
  new Button(x, y, w, h, message, onPress)
  ```
  par :
  ```java
  Button.builder(message, onPress).bounds(x, y, w, h).build()
  ```
- **Erreur sur `CreativeModeTab.TAB_MISC`** : remplace par
  `CreativeModeTab.TAB_DECORATIONS`.

Colle-moi le message d'erreur exact et je corrige.

## Idées d'amélioration possibles (à demander si tu veux)
- Limiter certains items "trop puissants" (netherite, items de boss...).
- Prix variable par item plutôt qu'un taux fixe.
- Bouton "acheter x10".
- Catégorie dédiée dans JEI.

