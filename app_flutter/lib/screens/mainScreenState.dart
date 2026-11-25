import 'package:flutter/material.dart';

class MainScreenState extends StatefulWidget {
  const MainScreenState({super.key});

  @override
  State<MainScreenState> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreenState> {
  String _busqueda = '';
  String _elementoSeleccionado = '';
  bool _mostrarCercaDeTi = false;
  bool _mostrarRecursos = false;
  bool _mostrarMunicipios = false;
  bool _mostrarOtrosRecursos = false;

  @override
  Widget build(BuildContext context) {
    final double ancho = MediaQuery.of(context).size.width;
    final Color fondo = Theme.of(context).colorScheme.primaryContainer;
    final bool todosLosFiltrosAbiertos =
        _mostrarCercaDeTi &&
        _mostrarRecursos &&
        _mostrarMunicipios &&
        _mostrarOtrosRecursos;

    return Scaffold(
      backgroundColor: fondo,
      appBar: AppBar(
        backgroundColor: fondo,
        title: TextField(
          onChanged: (texto) {
            setState(() {
              _busqueda = texto;
              _elementoSeleccionado = '';
              _mostrarCercaDeTi = texto.isNotEmpty;
              _mostrarRecursos = texto.isNotEmpty;
              _mostrarMunicipios = texto.isNotEmpty;
              _mostrarOtrosRecursos = texto.isNotEmpty;
            });
          },
          decoration: const InputDecoration(
            hintText: 'Buscar',
            prefixIcon: Icon(Icons.search),
            border: InputBorder.none,
          ),
        ),
        actions: [
          IconButton(
            onPressed: () {
              setState(() {
                final bool desplegar = !todosLosFiltrosAbiertos;
                _mostrarCercaDeTi = desplegar;
                _mostrarRecursos = desplegar;
                _mostrarMunicipios = desplegar;
                _mostrarOtrosRecursos = desplegar;
              });
            },
            icon: Icon(
              todosLosFiltrosAbiertos ? Icons.expand_less : Icons.expand_more,
            ),
          ),
        ],
      ),
      body: Container(
        color: fondo,
        width: ancho,
        child: ancho > 820
            ? Row(
                children: [
                  SizedBox(width: 360, child: _panelFiltros()),
                  SizedBox(width: ancho - 360, child: _panelDetalle()),
                ],
              )
            : CustomScrollView(
                slivers: [
                  SliverToBoxAdapter(child: _tarjetaFiltros()),
                  if (_elementoSeleccionado.isNotEmpty)
                    SliverToBoxAdapter(child: _detalleMovil()),
                ],
              ),
      ),
    );
  }

  Widget _panelFiltros() {
    return CustomScrollView(
      slivers: [SliverToBoxAdapter(child: _tarjetaFiltros())],
    );
  }

  Widget _tarjetaFiltros() {
    return Card(
      child: Column(
        children: [
          ListTile(
            leading: const Icon(Icons.location_on_outlined),
            title: const Text('Cerca de ti'),
            subtitle: const Text('Datos de muestra'),
            trailing: Icon(
              _mostrarCercaDeTi
                  ? Icons.keyboard_arrow_up
                  : Icons.keyboard_arrow_down,
            ),
            onTap: () {
              setState(() {
                _mostrarCercaDeTi = !_mostrarCercaDeTi;
              });
            },
          ),
          if (_mostrarCercaDeTi && _coincide('Recurso cercano de ejemplo'))
            _fila('Recurso cercano de ejemplo'),
          ListTile(
            title: const Text('Recursos'),
            trailing: Icon(
              _mostrarRecursos
                  ? Icons.keyboard_arrow_up
                  : Icons.keyboard_arrow_down,
            ),
            onTap: () {
              setState(() {
                _mostrarRecursos = !_mostrarRecursos;
              });
            },
          ),
          if (_mostrarRecursos && _coincide('Entidad de ejemplo'))
            _fila('Entidad de ejemplo'),
          if (_mostrarRecursos && _coincide('Centro de ejemplo'))
            _fila('Centro de ejemplo'),
          if (_mostrarRecursos && _coincide('Servicio de ejemplo'))
            _fila('Servicio de ejemplo'),
          ListTile(
            title: const Text('Municipios'),
            trailing: Icon(
              _mostrarMunicipios
                  ? Icons.keyboard_arrow_up
                  : Icons.keyboard_arrow_down,
            ),
            onTap: () {
              setState(() {
                _mostrarMunicipios = !_mostrarMunicipios;
              });
            },
          ),
          if (_mostrarMunicipios && _coincide('Alzira')) _fila('Alzira'),
          if (_mostrarMunicipios && _coincide('Algemesi')) _fila('Algemesi'),
          if (_mostrarMunicipios && _coincide('Cullera')) _fila('Cullera'),
          ListTile(
            title: const Text('Otros recursos'),
            trailing: Icon(
              _mostrarOtrosRecursos
                  ? Icons.keyboard_arrow_up
                  : Icons.keyboard_arrow_down,
            ),
            onTap: () {
              setState(() {
                _mostrarOtrosRecursos = !_mostrarOtrosRecursos;
              });
            },
          ),
          if (_mostrarOtrosRecursos && _coincide('Otro recurso'))
            _fila('Otro recurso'),
        ],
      ),
    );
  }

  Widget _fila(String titulo) {
    return ListTile(
      leading: Checkbox(
        value: _elementoSeleccionado == titulo,
        onChanged: (marcado) {
          setState(() {
            _elementoSeleccionado = marcado == true ? titulo : '';
          });
        },
      ),
      title: Text(titulo),
      trailing: const Text('Detalle'),
      onTap: () {
        setState(() {
          _elementoSeleccionado = titulo;
        });
      },
    );
  }

  Widget _panelDetalle() {
    return Card(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          if (_elementoSeleccionado.isEmpty)
            const Text('Selecciona un filtro para ver la vista previa')
          else
            _contenidoDetalle(),
        ],
      ),
    );
  }

  Widget _detalleMovil() {
    return Card(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [const Text('Vista previa'), _contenidoDetalle()],
      ),
    );
  }

  Widget _contenidoDetalle() {
    return Column(
      children: [
        Text(
          _elementoSeleccionado,
          style: Theme.of(context).textTheme.titleLarge,
        ),
        const Text('Detalle de muestra para la vista del directorio.'),
      ],
    );
  }

  bool _coincide(String texto) {
    return _busqueda.isEmpty ||
        texto.toLowerCase().contains(_busqueda.toLowerCase());
  }
}
