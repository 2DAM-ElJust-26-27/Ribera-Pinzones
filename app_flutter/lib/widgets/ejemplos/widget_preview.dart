import 'package:flutter/material.dart';
import 'package:flutter/widget_previews.dart';

@Preview(name: 'Widget Comptador')
Widget previsualitzacioDelWidget() => const ComptadorPreview();


class ComptadorPreview extends StatefulWidget {
  final int initialValue;
  final int minValue;
  final int maxValue;
  final Axis aligment;

  const ComptadorPreview({
    super.key,
    this.initialValue = 0,
    this.maxValue = 6,
    this.minValue = 0,
    this.aligment = Axis.horizontal
    });

  @override
  State<ComptadorPreview> createState() => _ComptadorPreview();
}



class _ComptadorPreview extends State<ComptadorPreview>{

  late int comptador;

  @override
  void initState() {
    super.initState();
    comptador = widget.initialValue;
  }

  @override
  Widget build(BuildContext context) {
    
    final ContingutWidget = <Widget>[
      FloatingActionButton(
        onPressed:() {
          setState(() {
            if (comptador < widget.maxValue) {comptador++;}}
          );
        },
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),

      Text(
        '$comptador',
        style: TextStyle(fontSize: 50),
      ),

      FloatingActionButton(
        onPressed:() {
          setState(() {
            if (comptador > widget.minValue) {comptador--;}}
          );
        },
        tooltip: 'Decrease',
        child: const Icon(Icons.remove),
      ),
    ];


    return widget.aligment == Axis.horizontal
      ? Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: ContingutWidget
        )
      : Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: ContingutWidget
        );
  }    
        
}