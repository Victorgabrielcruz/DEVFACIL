import 'package:flutter/material.dart';

import 'presentation/controllers/app_state.dart';
import 'presentation/screens/auth_screen.dart';
import 'presentation/screens/home_screen.dart';
import 'presentation/theme/app_theme.dart';

class DevFacilApp extends StatefulWidget {
  const DevFacilApp({
    super.key,
    this.initialState,
    this.restoreSessionOnStart = true,
  });

  final AppState? initialState;
  final bool restoreSessionOnStart;

  @override
  State<DevFacilApp> createState() => _DevFacilAppState();
}

class _DevFacilAppState extends State<DevFacilApp> {
  late final AppState state;

  @override
  void initState() {
    super.initState();
    state = widget.initialState ?? AppState();
    if (widget.restoreSessionOnStart) {
      state.restoreSession();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'DevFacil',
      theme: AppTheme.light(),
      home: AnimatedBuilder(
        animation: state,
        builder: (context, _) {
          if (!state.isAuthenticated) {
            return AuthScreen(state: state);
          }
          return HomeScreen(state: state);
        },
      ),
    );
  }
}
