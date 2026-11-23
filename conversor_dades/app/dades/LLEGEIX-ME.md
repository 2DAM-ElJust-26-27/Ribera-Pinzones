# Dades inicials · Sprint 1 · Versió 2

Combinem XML, CSV i JSON per poblar el nostre disseny PostgreSQL. Les dades són un conjunt inicial docent procedent del recull i de fonts identificades, no un cens exhaustiu ni un servei d’informació social validat. No inclouen notes internes, qüestionaris, credencials ni exportacions de la demo. No publiquem aquest prototip com a directori oficial.

| Fitxer | Contingut i relacions |
| --- | --- |
| `territori.csv` | 47 municipis: 35 de la Ribera Alta i 12 de la Ribera Baixa. Cada fila inclou `comarca_codi` i `comarca_nom`; creem cada comarca una sola vegada. No és un inventari de tots els nuclis i pedanies. |
| `catalegs.csv` | Valors de `forma_juridica`, `tipus_centre` i `collectiu`, diferenciats per la columna `tipus`. |
| `entitats.xml` | Organitzacions amb atribut `codi`, una adreça opcional, forma jurídica i sector coneguts; `<contactes>` i `<horaris>` incrustats dins de cada entitat. |
| `centres.json` | Centres amb una adreça única, `contactes` i `horaris` propis. `titular_codi` i `gestor_codi` referencien entitats; `municipi_codi`, el territori. Les referències desconegudes són nul·les. |
| `serveis.json` | Serveis amb `collectius`, `contactes`, `horaris` i `prestacions`. Cada prestació referencia una entitat amb `entitat_codi` i conté una llista `centres`, amb `centre_codi` i `font_codi`. No dupliquem els documents dels centres. |
| `fonts.json`, `manifest.json`, `comprovacions.json` | Procedència, recomptes i sumes SHA-256; conjunts esperats de consultes sobre aquesta versió. |

## Codis i valors

Els codis `ENT…`, `CEN…`, `SER…`, `MUN…` i els dels catàlegs són **codis d’origen**, no ID de PostgreSQL ni codis oficials INE. Els conservem perquè els documents es referencien. No assignem codis propis als contactes, horaris, franges ni prestacions incrustats: PostgreSQL generarà els ID. La prestació s’identifica en la importació per la parella servei contenidor–entitat referenciada. Les noves fitxes podran existir sense codi d’origen.

La font estable d’importació és `ribera-sprint1` i el manifest indica la versió 2. Resolem font + tipus de registre + codi a l’ID generat, sense retallar prefixos ni relacionar pel nom. Si ja havíem importat l’antic paquet amb l’etiqueta `ribera-sprint1-v1`, reconciliem aquella correspondència o reiniciem la base de prova: no tractem la reorganització com si foren entitats noves. Distingim aquesta identitat d’importació dels codis de procedència `F…`/`W…` de `fonts.json`.

Tots els fitxers són UTF-8. En CSV utilitzem coma, primera fila de capçalera i cometes dobles amb l’escapat habitual; el valor reservat `\N` representa nul i no és el text que guardarem. Una cel·la buida és una cadena buida. Llegim amb un parser CSV, sense `split(",")`. En JSON usem `null`; en XML un element opcional absent equival a nul. Els codis postals són text i no identifiquen municipis; no proporcionem ara un carrerer ni un catàleg complet de CP.

En JSON, `collectius` és una llista de codis i genera una relació múltiple, no una cadena amb comes. Les hores, quan n’hi haja en futures dades, tindran format `HH:MM` i dies ISO d’1 (dilluns) a 7 (diumenge). Un element de `franges` tindria `dia_setmana`, `hora_inici`, `hora_fi` i `final_dia_seguent`. No hi afegim files buides si la llista és buida. El text de l’horari pot complementar les franges: «amb cita» no és una hora.

`territori.csv` proporciona municipis per als filtres de consulta. Les files repeteixen la comarca com a dada d’origen: la normalitzem en una taula relacionada i comprovem que un mateix codi de comarca no tinga noms contradictoris. La Barraca d’Aigües Vives i altres nuclis no són municipis independents. Alfarb i Montroi segueixen la denominació de la Mancomunitat de la Ribera Alta; podem trobar Alfarp o Montroy en altres fonts. La comarca no implica cobertura de tots els serveis.

## Documents incrustats i referències

En XML cada `<contacte>` està dins de `<contactes>` de la seua entitat i cada `<horari>`, dins de `<horaris>`. Un contenidor buit significa que no aportem registres fills, no que hàgem de crear una fila buida. `<franges/>` equival a `franges: []` en JSON. En aquesta versió hi ha 9 contactes i 19 horaris repartits entre els tres documents principals; els horaris són text documental sense intervals exactes confirmats.

En serveis, aquesta estructura abreujada mostra una prestació i el centre que referencia:

```json
{
  "codi": "SER12",
  "prestacions": [
    {
      "entitat_codi": "ENT02",
      "font_codi": "W03",
      "centres": [{"centre_codi": "CEN05", "font_codi": "W03"}]
    }
  ]
}
```

El servei s’insereix una sola vegada; després inserim o recuperem la prestació i relacionem el seu ID amb el del centre. No inserim una còpia del centre per cada aparició. Les dades incrustades continuen generant les taules relacionades del nostre disseny: el format JSON/XML no obliga a guardar-les com a JSON dins de PostgreSQL.

## Importació i comprovació

Carreguem `catalegs.csv` i `territori.csv`, després `entitats.xml`, `centres.json` i `serveis.json`. Després d’inserir cada pare recuperem el seu ID per inserir els contactes, horaris i altres fills. Una franja dependria de l’ID de l’horari, no d’un codi textual propi. Les prestacions ja poden resoldre les entitats i centres carregats. Si falta una referència, informem del fitxer i del camí concret, per exemple `serveis[10].prestacions[0].centres[0].centre_codi`, sense fabricar registres.

Per repetir la càrrega, definim una política sense duplicats ni pèrdua de correccions amb nuls. Conservem la unicitat de les parelles entitat–servei i prestació–centre i decidim com reconciliar els fills incrustats dins d’una transacció; inserir-los de nou indiscriminadament els duplicaria. Provem una càrrega des de zero i una repetició controlada.

Podem guardar un servei encara que no coneguem el prestador. Això no autoritza a crear una prestació amb entitat fictícia. No tindre una relació prestació–centre tampoc demostra que un servei siga domiciliari: la ubicació pot ser desconeguda. Les fonts semblants no acrediten que dos programes siguen el mateix. Salut mental greu i discapacitat intel·lectual són col·lectius diferents.

Les adreces i els col·lectius documentals requereixen revisió futura; no són garanties d’admissió. S’ha exclòs el llindar contradictori de discapacitat de Jugue amb tú. Els noms descriptius de serveis no es presenten com a denominacions oficials. El CEAM de Cullera s’associa a la Generalitat i al seu servei segons la fitxa oficial de la unitat i les seues funcions; això no assigna automàticament la propietat de l’edifici. Els camps de titular i gestor es mantenen nuls quan no estan acreditats.

El punt de CEN05 és el publicat pel mapa de la fitxa GVA, amb `precisio_punt=punt_publicat`, no una mesura GPS verificada per nosaltres. La resta de coordenades desconegudes són nul·les: no usem `(0,0)`. Per a la demostració podem completar altres punts amb una font documentada i revisió explícita, sense modificar les fonts inicials. No cal seleccionar carrers ni implementar edició d’adreces en aquest sprint.

`comprovacions.json` compara conjunts de codis d’origen, no ID finals ni ordre de resultats. Filtrar «prestat a Cullera» recorre prestació–centre–municipi, sense confondre ubicació amb cobertura. Un conjunt esperat buit només descriu aquestes dades, no l’absència real de recursos a la comarca. No inventem registres per canviar el resultat.

Conservem la procedència indicada en `fonts.json`. Els municipis provenen de la Mancomunitat de la Ribera Alta i del portal oficial de Turisme de la Comunitat Valenciana. Els canals d’AFASU i SASM i la fitxa del CEAM provenen de les webs de les organitzacions. El paquet no inclou fotografies ni descàrregues de mapes; si usem cartografia externa, mantenim-ne l’atribució i respectem les seues condicions.
