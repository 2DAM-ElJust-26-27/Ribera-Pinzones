import 'package:flutter/material.dart';
import 'package:flutter/widget_previews.dart';


@Preview(name: 'Widget GridView')
Widget previsualitzacioDelWidget() => const ExempleGridView();

class ExempleGridView extends StatefulWidget {
  const ExempleGridView({super.key});

  @override
  State<ExempleGridView> createState() => _ExempleGridView();
}

class _ExempleGridView extends State<ExempleGridView> {

  final List<String> items = [
    "Element 1", 
    "Element 2", 
    "Element 3",
    "Element 4", 
    "Element 5", 
    "Element 6",
  ];

  
  @override
  Widget build(BuildContext context) {
    return GridView.builder(
      itemCount: items.length,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
        childAspectRatio: 2.5, // relació ample/alt de cada casella
      ),
      itemBuilder: (BuildContext context, int index) {
        return Container(
          alignment: Alignment.center,
          child: Text(items[index]),
        );
      },
    );
  }
}