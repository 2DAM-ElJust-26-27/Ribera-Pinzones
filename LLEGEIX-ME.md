# Base de l’activitat de contenidors

Aquest paquet aporta una API mínima de salut amb Javalin i Java 21, el seu Dockerfile i un script que crea l’esquema PostgreSQL `ribera`. No cal programar Java per fer l’activitat i encara no hi ha taules ni rutes del directori.

Creem `compose.yaml` seguint «Pràctica Cloud · Contenidors del projecte» de l’enunciat. Copiem `.env.example` a `.env` i canviem la contrasenya abans de la primera arrancada. No publiquem `.env` ni incloem credencials en captures.

Les rutes parteixen de la carpeta que conté aquest README: el context de construcció de l’API és `./api` i el directori d’inicialització PostgreSQL és `./postgres/init`. El Dockerfile utilitza Gradle dins de la construcció; no necessitem instal·lar-lo en l’ordinador per construir la imatge. El Compose no està inclòs perquè el preparem com a exercici a partir de les instal·lacions anteriors.

`/api/estat` comprova HTTP i `/api/salut` comprova també la connexió JDBC. Aquest últim retorna 200 amb la base disponible i 503 si falla. La classe `org.connectantribera.infra.ComprovaSalut` serveix com a healthcheck HTTP del contenidor.
