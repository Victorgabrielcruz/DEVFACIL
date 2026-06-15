import 'package:devfacil_flutter/src/app.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('renderiza app DevFacil', (tester) async {
    await tester.pumpWidget(
      const DevFacilApp(restoreSessionOnStart: false),
    );

    expect(find.text('DevFacil'), findsOneWidget);
  });
}
