package app.foff;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * While this screen is open, the phone responds to NFC readers
 * only as type A and B (Visa/Mastercard) and not as type F (FeliCa).
 * When the screen closes, everything goes back to how it was.
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
            text.setText("This phone does not have NFC.");
            return;
        }
        if (!nfc.isEnabled()) {
            text.setText("NFC is off. Turn it on and reopen the app.");
            return;
        }
        try {
            nfc.setDiscoveryTechnology(
                    this,
                    NfcAdapter.FLAG_READER_KEEP,
                    NfcAdapter.FLAG_LISTEN_NFC_PASSIVE_A | NfcAdapter.FLAG_LISTEN_NFC_PASSIVE_B);
            text.setText("FeliCa (type F) OFF.\n\n"
                    + "Keep this screen open and hold the phone near the payment terminal.\n\n"
                    + "Closing the app restores everything to how it was.");
        } catch (Exception e) {
            text.setText("Unable to turn off type F on this phone.\n\n" + e);
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
