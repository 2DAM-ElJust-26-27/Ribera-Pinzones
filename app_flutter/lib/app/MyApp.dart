import 'package:app/screens/mainScreenState.dart';
import 'package:flutter/material.dart';

class Myapp {
  MaterialApp build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Ribbera Connectada',
      home: mainScreenState(),
    );
  }
}
