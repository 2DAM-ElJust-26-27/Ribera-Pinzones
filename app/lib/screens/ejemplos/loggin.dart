import 'package:flutter/material.dart';
import 'package:flutter/widget_previews.dart';

@Preview()
Widget widget() => Loggin();



class Loggin extends StatefulWidget {
  const Loggin({super.key});

  @override
  State<Loggin> createState() => LogginState();
}



class LogginState extends State<Loggin> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      
      home: Scaffold(
        appBar: AppBar(title: const Text('Loggin'), centerTitle: true),

        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              
              children: [
                
                TextField(
                  keyboardType: TextInputType.text,
                  maxLength: 4,
                  obscureText: false,
                  obscuringCharacter: "*",
                  //readOnly: true,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                    ),
                    icon: const Icon(Icons.person),
                    hintText: 'Introduce tu usuario',
                    labelText: 'Usuario',
                  ),
                ),

                const SizedBox (height: 16),

                TextField(
                  keyboardType: TextInputType.text,
                  maxLength: 4,
                  obscureText: true,
                  obscuringCharacter: "*",
                  //readOnly: true,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                    ),
                    icon: const Icon(Icons.key),
                    hintText: 'Introduce la contraseña',
                    labelText: 'Contraseña',
                    suffixIcon: const Icon(Icons.remove_red_eye),
                  ),
                ),

                const SizedBox (height: 16),
                
                ElevatedButton(
                  onPressed: null,
                  child: const Text('Mostrar credencials')
                ),
                
              ],
            ),
          ),
        ),
      ),
    );
  }
}
