package app.foff;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Finché questa schermata è aperta, il telefono risponde ai lettori NFC
 * solo come tipo A e B (Visa/Mastercard) e non come tipo F (FeliCa).
 * Quando la schermata si chiude, tutto torna come prima.
 */
public class MainActivity extends Activity {

    private TextView text;
    private NfcAdapter nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = new TextView(this);
        text.setTextSize(22);
        text.setPadding(60, 200, 60, 60);
        setContentView(text);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        nfc = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfc == null) {
            text.setText("Questo telefono non ha l'NFC.");
            return;
        }
        if (!nfc.isEnabled()) {
            text.setText("L'NFC è spento. Accendilo e riapri l'app.");
            return;
        }
        try {
            nfc.setDiscoveryTechnology(
                    this,
                    NfcAdapter.FLAG_READER_KEEP,
                    NfcAdapter.FLAG_LISTEN_NFC_PASSIVE_A | NfcAdapter.FLAG_LISTEN_NFC_PASSIVE_B);
            text.setText("FeliCa (tipo F) SPENTO.\n\n"
                    + "Tieni aperta questa schermata e avvicina il telefono al POS.\n\n"
                    + "Chiudendo l'app tutto torna come prima.");
        } catch (Exception e) {
            text.setText("Non riesco a spegnere il tipo F su questo telefono.\n\n" + e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfc != null) {
            try {
                nfc.resetDiscoveryTechnology(this);
            } catch (Exception ignored) {
            }
        }
    }
}
