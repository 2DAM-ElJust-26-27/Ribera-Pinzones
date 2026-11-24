import 'package:app/screens/mainScreenState.dart';
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _MyAppState();
}

class _MyAppState {
  int _selectedIndex = 0;
  late Future<List<Cataleg>> _cataleg;
  late Future<List<Centres>> _centres;
  late Future<List<Contactes>> _contactes;
  late Future<List<Entitats>> _entitats;
  late Future<List<Horaris>> _horaris;
  late Future<List<Serveis>> _serveis;
  late Future<List<Territori>> _territori;

  @override
  void initState() {
    super.initState();
    _cataleg = _repository.getCataleg();
    _centres = _repository.getCentres();
    _contactes = _repository.getContactes();
    _entitats = _repository.getEntitats();
    _horaris = _repository.getHoraris();
    _serveis = _repository.getServeis();
    _territori = _repository.getTerritori();
  }
  
}

class Repositori {
    late String urlBase;
    late RiberaApi api;

    Repositori(this.urlBase) {
        this.urlBase = urlBase;
        this.api = RiberaApi(urlBase);
    }

    Future <List<Cataleg>> llistarCataleg() async {
        return (await api.llistarCataleg()).map(((cataleg) {
            return Cataleg.fromJson(cataleg, urlBase);
        })).toList();
    }


}
