import 'package:flutter/material.dart';
import 'package:flutter/widget_previews.dart';

@Preview()
Widget widget() => ListTileApp();

class ListTileApp extends StatelessWidget {
  const ListTileApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: ListTileExample());
  }
}

class ListTileExample extends StatelessWidget {
  const ListTileExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mensajes')),
      body: ListView(
        children: const <Widget>[
          
          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.greenAccent,
                child: Text('M'),
              ),
              
              title: Text('Mama'),
              subtitle: Text('Ves a comprar pan que mañana cierran'),
              
              trailing: Icon(Icons.more_vert),
            ),
          ),


          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.cyan,
                child: Text('H'),
              ),
              
              title: Text('Hacienda'),
              subtitle: Text('Mañana te embargamos la casa'),
              
              trailing: Icon(Icons.more_vert),
            ),
          ),


          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.redAccent,
                child: Icon(Icons.person),
              ),
              
              title: Text('Timador Profesional'),
              subtitle: Text('Tu repartidor de amazon ha sido secuestrado, envia 500€ a este num...'),
              
              trailing: Icon(Icons.more_vert),
            ),
          ),


          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.purpleAccent,
                child: Icon(Icons.contacts_sharp),
              ),
              
              title: Text('Grupo Amigos'),
              subtitle: Text('Juanjo: mañana alguien para un furbito?'),
              
              trailing: Icon(Icons.more_vert),
            ),
          ),


          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.tealAccent,
                child: Text('G'),
              ),
              
              title: Text('Gym Bro'),
              subtitle: Text('Hoy toca pierna :('),
              
              trailing: Icon(Icons.more_vert),
            ),
          ),


          Card(
            child: ListTile(
              
              leading: CircleAvatar(
                backgroundColor: Colors.amber,
                child: Icon(Icons.person),
              ),
              
              title: Text('Antonio Lobato'),
              subtitle: Text('Quieres saber cuando vale tu coche?'),
              
              trailing: Icon(Icons.more_vert),
            ),
          )
        ],
      ),
    );
  }
}
