import 'package:flutter/material.dart';
import 'package:app/widgets/ejemplos/widget_preview.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Comptador',
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(title: const Text('Text de la barra')),

        body: Center(
          child: ComptadorPreview(
            initialValue: 0,
            minValue: 0,
            maxValue: 6,
            aligment: Axis.horizontal,
          ),
        ),
      ),
    );
  }
}
