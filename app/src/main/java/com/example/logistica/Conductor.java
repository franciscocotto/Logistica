package com.example.logistica;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Conductor extends Fragment {

    private static String path;
    public Conductor(String path) {
        // Required empty public constructor
        this.path = path;
    }
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ARG_PARAM1 = "param1";
    static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String mParam1;
    String mParam2;

    OnFragmentInteractionListener mListener;
    final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    //final String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    final int MIS_PERMISOS = 100;
    final int COD_SELECCIONA = 10;
    final int COD_FOTO = 20;

    EditText txtNombre,txtApellido,txtNumContac,txtDui,txtNit,txtTipoLicencia,txtNumLicencia;
    Button botonFoto, botonRegistrar;
    ImageView imgFoto;
    ProgressDialog progreso;
    RelativeLayout layoutRegistrar;//permisos
    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrarUsuarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Conductor newInstance(String param1, String param2) {
        Conductor fragment = new Conductor(path);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.driver_fragment, container, false);
        View vista=inflater.inflate(R.layout.driver_fragment, container, false);
        //EditText txtNombre,txtApellido,txtNumContac,txtDui,txtNit,txtTipoLicencia,txtNumLicencia;
        txtNombre= (EditText) vista.findViewById(R.id.addNameCon);
        txtApellido= (EditText) vista.findViewById(R.id.addApeCon);
        txtNumContac= (EditText) vista.findViewById(R.id.addTelCon);
        txtDui= (EditText) vista.findViewById(R.id.addDuiCon);
        txtNit= (EditText) vista.findViewById(R.id.addNitCon);
        txtTipoLicencia= (EditText) vista.findViewById(R.id.addTipoLic);
        txtNumLicencia= (EditText) vista.findViewById(R.id.addNumLic);
        botonFoto= (Button) vista.findViewById(R.id.btnFoto);
        botonRegistrar=(Button)vista.findViewById(R.id.btnRegistrar);
        imgFoto=(ImageView)vista.findViewById(R.id.imgFoto);
        layoutRegistrar= (RelativeLayout) vista.findViewById(R.id.idLayoutRegistrar);

        // request= Volley.newRequestQueue(getContext());
        //Permisos
        if(solicitaPermisosVersionesSuperiores()){
            botonFoto.setEnabled(true);
        }else{
            botonFoto.setEnabled(false);
        }
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });
        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogOpciones();
            }
        });return vista;
    }

    private void mostrarDialogOpciones() {
        final CharSequence[] opciones={"Tomar Foto","Elegir de Galeria","Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una Opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    abriCamara();
                }else{
                    if (opciones[i].equals("Elegir de Galeria")){
                        Intent intent=new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    private void abriCamara() {
        File miFile=new File(Environment.getExternalStorageDirectory(),DIRECTORIO_IMAGEN);
        boolean isCreada=miFile.exists();
        if(isCreada==false){
            isCreada=miFile.mkdirs();
        }
        if(isCreada==true){
            Long consecutivo= System.currentTimeMillis()/1000;
            String nombre=consecutivo.toString()+".jpg";
            path=Environment.getExternalStorageDirectory()+File.separator+DIRECTORIO_IMAGEN
                    +File.separator+nombre;//indicamos la ruta de almacenamiento
            fileImagen=new File(path);
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileImagen));
            ////
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities=getContext().getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent,COD_FOTO);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getContext(),"Permisos aceptados",Toast.LENGTH_SHORT);
                botonFoto.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case COD_SELECCIONA:
                Uri miPath=data.getData();
                imgFoto.setImageURI(miPath);

                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),miPath);
                    imgFoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path",""+path);
                            }
                        });

                bitmap= BitmapFactory.decodeFile(path);
                imgFoto.setImageBitmap(bitmap);
                break;
        }
        bitmap=redimensionarImagen(bitmap,600,800);
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {
        int ancho=bitmap.getWidth();
        int alto=bitmap.getHeight();
        if(ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho=anchoNuevo/ancho;
            float escalaAlto= altoNuevo/alto;
            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);
            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);
        }else{
            return bitmap;
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }
        //validamos si los permisos ya fueron aceptados
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }
        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    private void cargarWebService() {

        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip=getString(R.string.ip);
        String url=ip+"/BDRemota/wsJSONRegistroMovil.php?"; //<<<<<<<<<------------------ACACACA
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                if (response.trim().equalsIgnoreCase("registra")){
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtNumContac.setText("");
                    txtDui.setText("");
                    txtNit.setText("");
                    txtTipoLicencia.setText("");
                    txtNumLicencia.setText("");
                    Toast.makeText(getContext(),"Se ha registrado con exito",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"No se ha registrado ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String Nombre= txtNombre.getText().toString();
                String Apellido= txtApellido.getText().toString();
                String NumContac= txtNumContac.getText().toString();
                String Dui= txtDui.getText().toString();
                String Nit= txtNit.getText().toString();
                String TipoLicencia= txtTipoLicencia.getText().toString();
                String NumLicencia= txtNumLicencia.getText().toString();
                String imagen=convertirImgString(bitmap);
                Map<String,String> parametros=new HashMap<>();
                parametros.put("Nombre",Nombre);
                parametros.put("Apellido",Apellido);
                parametros.put("NumContac",NumContac);
                parametros.put("Dui",Dui);
                parametros.put("Nit",Nit);
                parametros.put("TipoLicencia",TipoLicencia);
                parametros.put("NumLicencia",NumLicencia);
                parametros.put("imagen",imagen);
                return parametros;
            }
        };
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
