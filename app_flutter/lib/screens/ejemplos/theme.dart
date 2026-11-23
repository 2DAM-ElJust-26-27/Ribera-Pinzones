import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart'; // Fonts de Google

// ------------------------------------------------------
// Treballant amb temes de Material 3, amb tipografies de Google Fonts
// L'exemple funciona amb DartPad i online.
// Deixem comentada la versió amb fontFamily/TextTheme locals
// per a projectes reals amb assets. Aci farem ús de GoogleFonts.
// ------------------------------------------------------


const _seed = Color(0xFFFFB300); // Color amber personalitzat

final ThemeData temaPersonalitzatLight = ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: _seed,
    brightness: Brightness.light,
  ),

  // Tipografia principal via Google Fonts
  textTheme: GoogleFonts.latoTextTheme().copyWith(
    // Exemple: títol gran amb una altra Google Font
    titleLarge: GoogleFonts.pacifico(
      fontSize: 36,
      fontWeight: FontWeight.w400,
    ),
    // Ajust de mida més sensat per a la demo
    displayMedium: GoogleFonts.lato(fontSize: 20),
  ),

  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
    ),
  ),
);

final ThemeData temaPersonalitzatDark = ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: _seed,
    brightness: Brightness.dark,
  ),
  textTheme: GoogleFonts.latoTextTheme().copyWith(
    titleLarge: GoogleFonts.pacifico(
      fontSize: 36,
      fontWeight: FontWeight.w400,
    ),
    displayMedium: GoogleFonts.lato(fontSize: 20),
  ),
);

// Variant 2: altre seed (substitut modern de primarySwatch)
final ThemeData temaPersonalitzat2 = ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.lime),
  textTheme: GoogleFonts.latoTextTheme(),
);

// Variant 3: equivalent simplificat via colorSchemeSeed
final ThemeData temaPersonalitzat3 = ThemeData(
  useMaterial3: true,
  colorSchemeSeed: Colors.amber,
  textTheme: GoogleFonts.latoTextTheme(),
);

/* ------------------------------------------------------
   VERSIÓ AMB FONTS LOCALS (PROJECTE REAL, NO DARTPAD)
   - Declarem les fonts al pubspec.yaml i col·loquem els .ttf a assets/fonts
   - Substitïm la secció textTheme de dalt per aquesta:
   
   fontFamily: 'Arimo',
   textTheme: const TextTheme(
     displayLarge: TextStyle(fontSize: 72, fontWeight: FontWeight.bold),
     titleLarge: TextStyle(fontSize: 36, fontFamily: 'Pacifico'),
     displayMedium: TextStyle(fontSize: 20, fontFamily: 'Pacifico'),
     bodyMedium: TextStyle(fontSize: 14),
   ),
------------------------------------------------------- */

void main() {
  runApp(const ExempleFonts());
}

class ExempleFonts extends StatelessWidget {
  const ExempleFonts({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      themeMode: ThemeMode.system,
      // theme: temaPersonalitzatLight,
      // darkTheme: temaPersonalitzatDark,

      // Per provar ràpid les variants, comenta les dues línies de dalt
      // i descomenta una d'estes:
      theme: temaPersonalitzat2,
      // theme: temaPersonalitzat3,

      debugShowCheckedModeBanner: false,
      title: 'Exemple de temes',
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Títol de la barra'),
          centerTitle: true,
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  'Això és un text',
                  style: Theme.of(context).textTheme.displayMedium,
                ),
                const SizedBox(height: 16),

                // Tema local només per al botó, sense carregar-te la resta
                Theme(
                  data: Theme.of(context).copyWith(
                    elevatedButtonTheme: ElevatedButtonThemeData(
                      style: ElevatedButton.styleFrom(
                        shape: const StadiumBorder(),
                      ),
                    ),
                  ),
                  child: ElevatedButton(
                    onPressed: () {},
                    child: const Text('I això un botó'),
                  ),
                ),

                const SizedBox(height: 16),

                Text(
                  'Primari: ${Theme.of(context).colorScheme.primary}',
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}