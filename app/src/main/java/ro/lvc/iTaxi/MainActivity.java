package ro.lvc.iTaxi;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {
    WebView mWebview    =   null;
    String theUrl       =   "https://itaxilvc.github.io/";
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);
        mWebview  = findViewById(R.id.webviewmain);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file");
                String fileName = contentDisposition.replace("inline; filename=", "");
                fileName = fileName.replaceAll(".+UTF-8''", "");
                fileName = fileName.replaceAll("\"", "");
                fileName = URLDecoder.decode(fileName, "UTF-8");
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    Toast.makeText(getBaseContext(), "Permission Error", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            110);
                }
            }
        });
        mWebview.setWebViewClient(new WebViewClient(){
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                                          if(url.startsWith("tel:") || url.startsWith("whatsapp:") || url.startsWith("mailto:") || url.startsWith("intent:")) {
                                              Intent intent = new Intent(Intent.ACTION_VIEW);
                                              intent.setData(Uri.parse(url));
                                              startActivity(intent);
                                              return true;
                                          }
                                          return false;
                                      }
                                  });


        mWebview .loadUrl(theUrl);



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}