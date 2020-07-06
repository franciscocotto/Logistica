package com.example.logistica.ui.driver;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Administrador;
import com.example.logistica.BuildConfig;
import com.example.logistica.ConMapsActivity;
import com.example.logistica.HandlerService;
import com.example.logistica.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Conductormod extends Fragment implements AdapterView.OnItemSelectedListener{

    public Conductormod() {
        // Required empty public constructor
        //fgdfgfdgd
    }

    //Declaramos las variables para cada elemento que vamos a obtener desde nuestro fragment driver
    private EditText id_conductor, id_url, edNombre, edApellido, edTelefono,edNDUI, edNit, edNumLicencia, edDireccion;
    private int id_idi;
    Button btnEliminar, btnRegresar, btnEditar;
    //Declaramos variables para el envío de datos al webService
    HttpClient cliente;
    HttpPost post;
    List<NameValuePair> lista;
    ///--------------------------------------------------------------------------------------------------
    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";

    File fileImagen;
    Bitmap bitmap;

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;
    ProgressDialog progreso;

    RelativeLayout layoutRegistrar;//permisos
    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    StringRequest stringRequest;

    Button botonCargar;
    ImageView imagen;
    String path;//almacena la ruta de la imagen
    RequestQueue request;
    ///--------------------------------------------------------------------------------------------------

    private Spinner spinnerTipoLicencia;
    private ArrayList<Licencia> tipoLicenciaList;
    ProgressBar progressBar;
    ProgressDialog pDialog;

    /** Servicios Web Consumidos **/
    /*Declaramos variables para almacernar la dirección que apunta hacia el web service
    En este caso declaramos una para editar alojada en WebHost000 apuntando a la carpeta VC17009 que contiene mi servicio
    para editar conductores */
    private String URL_REGISTRAR ="https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/registrarConductor.php";
    private String URL_TIPOLICENCIA = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/getTiposLicencia.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Asociamos la clase actual al layout fragment_con_documentos
        Cabe destacar que se maneja como una vista, con el objetivo de realizar la carga del fragment
        dentro del Contentedor principal Conductores */
        View view = inflater.inflate(R.layout.drivermod_fragment, container, false);
        /*----------------Inicializació de variables----------------------*/
        progressBar = view.findViewById(R.id.progressBar);
        //Inicializamos los spinner que muestran las categorias y los idiomas
        spinnerTipoLicencia = (Spinner) view.findViewById(R.id.addTipoLic);
        //get other data form
        //EditText edNombre, edApellido, edTelefono,edNDUI, edNit, edNumLicencia;
        id_conductor = (EditText) view.findViewById(R.id.id_conductor);
        id_url = (EditText) view.findViewById(R.id.id_url);
        edNombre = (EditText) view.findViewById(R.id.addNameCon);
        edApellido = (EditText) view.findViewById(R.id.addApeCon);
        edTelefono = (EditText) view.findViewById(R.id.addTelCon);
        edNDUI = (EditText) view.findViewById(R.id.addDuiCon);
        edNit = (EditText) view.findViewById(R.id.addNitCon);
        edNumLicencia = (EditText) view.findViewById(R.id.addNumLic);
        edDireccion= (EditText) view.findViewById(R.id.addDirCon);
        btnEditar = (Button) view.findViewById(R.id.btnREditar);
        btnRegresar = (Button) view.findViewById(R.id.btnRegresar);
        btnEliminar = (Button) view.findViewById(R.id.btnDelete);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();
        //Initializing the ArrayList
        tipoLicenciaList = new ArrayList<Licencia>();
        /*----------------Inicializació de variables----------------------*/
        //Call Actions
        spinnerTipoLicencia.setOnItemSelectedListener(this);
        new getTipoLicencias().execute();
        /**
         Con el siguiente método se obtienen los elementos del documento, mediante el OnClick al botón de Modificar
         luego de almacenar cada valor obtenido y almacenado en las variables, procedemos a verificas que no se encuentre
         vacío, con esta validación evitaremos ell envío de campos vacíos y obtener errores por NullPointException o errores en
         nuestro webService al no recibir la variable esperada.
         En caso de no cmplirse una validación el método retorna falso e impide avanzar al siguiente, si se cumple con todas
         se llama al método EnviarForm()
         * */
        view.findViewById(R.id.btnREditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombreCon = edNombre.getText().toString().trim();
                final String apellidoCon =  edApellido.getText().toString().trim();
                final String telefonoCon =  edTelefono.getText().toString().trim();
                final String numeroDui =  edNDUI.getText().toString().trim();
                final String numeroNIT =  edNit.getText().toString().trim();
                final String numeroLicencia =  edNumLicencia.getText().toString().trim();
                final String direccionCon =  edDireccion.getText().toString().trim();
                /**
                 * Validaciones
                 * */
                if (TextUtils.isEmpty(nombreCon)) {
                    edNombre.setError("Favor Ingresar Nombre");
                    edNombre.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(apellidoCon)) {
                    edApellido.setError("Favor Ingresar Apellido");
                    edApellido.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(telefonoCon)) {
                    edTelefono.setError("Favor Ingresar Telefono de Contacto");
                    edTelefono.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(numeroDui)) {
                    edNDUI.setError("Favor Ingresar número de DUI");
                    edNDUI.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(numeroNIT)) {
                    edNit.setError("Favor Ingresar número de NIT");
                    edNit.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(numeroLicencia)) {
                    edNumLicencia.setError("Favor Ingresar Numero de Licencia");
                    edNumLicencia.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(direccionCon)) {
                    edDireccion.setError("Favor Ingresar Direccion");
                    edDireccion.requestFocus();
                    return;
                }
                /** Llamamos al Método que realiza la función de guardar datos.  **/
                EnviarForm();
            }
        });

        //Método que recibe la acción OnClick luego se llama al método de confirmación

        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ConfirmarEliminarConductor();
            }
        });


        view.findViewById(R.id.btnRegresar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegresarBusqueda();
            }
        });

        imagen= (ImageView) view.findViewById(R.id.imgFoto);
        botonCargar= (Button) view.findViewById(R.id.btnFoto);

        request = Volley.newRequestQueue(getActivity());
        if(validaPermisos()){
            botonCargar.setEnabled(true);
        }else{
            botonCargar.setEnabled(true);
        }

        view.findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagen();
            }
        });
        return view;
    }

    private void cargarImagen(String urlImagen) {
       //urlImagen=urlImagen.replace(" ","%20");

        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
               // bitmap=response;//SE MODIFICA
                imagen.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error al cargar la imagen",Toast.LENGTH_SHORT).show();
                Log.i("ERROR IMAGEN","Response -> "+error);
            }
        });
          request.add(imageRequest);
       // VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(imageRequest);
    }

    @SuppressLint("WrongConstant")
    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        //android.Manifest.permission.
        if((checkSelfPermission(getContext(),CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(getContext(),WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                botonCargar.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getActivity());
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri= Uri.fromParts("package",getActivity().getApplicationContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
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

    public void onclick(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getActivity());
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarWebService() {
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();
        //String ip=getString(R.string.ip);
         String url="https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/UpdateConductor.php";
       // String url="http://192.168.0.27/VC17009/registrarConductor.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        consultarConductor consultarConductor = new consultarConductor();
                        FragmentTransaction fr = getFragmentManager().beginTransaction();
                        fr.replace(R.id.nav_host_fragment, new consultarConductor());
                        fr.commit();
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            //Ete error notifica errores en el web service
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final String setId = id_conductor.getText().toString().trim();
                final String setnombreCon = edNombre.getText().toString().trim();
                final String setapellidoCon =  edApellido.getText().toString().trim();
                final String settelefonoCon =  edTelefono.getText().toString().trim();
                final String setnumeroDui =  edNDUI.getText().toString().trim();
                final String setnumeroNIT =  edNit.getText().toString().trim();
                final String setDireccion =  edDireccion.getText().toString().trim();
                final String setnumeroLicencia =  edNumLicencia.getText().toString().trim();
                final String setd_idi =  spinnerTipoLicencia.getSelectedItem().toString().trim();
                try {
                    String imagen=convertirImgString(bitmap);

                    Map<String,String> parametros=new HashMap<>();
                    parametros.put("id_conductor", setId);
                    parametros.put("dui", setnumeroDui);
                    parametros.put("nombre", setnombreCon);
                    parametros.put("apellido", setapellidoCon);
                    parametros.put("nit", setnumeroNIT);
                    parametros.put("telefono", settelefonoCon);
                    parametros.put("direccion", setDireccion);
                    parametros.put("url_foto", "abvc");
                    parametros.put("licencia", setnumeroLicencia);
                    parametros.put("tipo_licencia", setd_idi);
                    parametros.put("imagen",imagen);
                    return parametros;
                }catch (Exception e){
                    final String seturl = "null";
                    Map<String,String> parametros=new HashMap<>();
                    parametros.put("id_conductor", setId);
                    parametros.put("dui", setnumeroDui);
                    parametros.put("nombre", setnombreCon);
                    parametros.put("apellido", setapellidoCon);
                    parametros.put("nit", setnumeroNIT);
                    parametros.put("url_foto", "abvc");
                    parametros.put("telefono", settelefonoCon);
                    parametros.put("direccion", setDireccion);
                    parametros.put("licencia", setnumeroLicencia);
                    parametros.put("tipo_licencia", setd_idi);
                    parametros.put("imagen",seturl);
                    return parametros;
                }

            }
        };
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
    }

    //Metodo para regresar a pantalla Busqueda de Documentos
    public  void RegresarBusqueda(){
        consultarConductor consultarConductor = new consultarConductor();
        FragmentTransaction fr = getFragmentManager().beginTransaction();
         fr.replace(R.id.nav_host_fragment, new consultarConductor());
         fr.commit();
    }

    //Método que notifica al usuario si esta seguro de confirmar eliminar
    public void ConfirmarEliminarConductor(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta Seguro que desea Eliminar el Conductor?");
        myBuild.setIcon(R.drawable.ic_error_outline_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EliminarConductor();
            }
        });
        myBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBuild.create();
        dialog.show();
    }

    //Método que consume el servicio para eliminar
    public void EliminarConductor(){
        final String URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/DeleteConductor.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        consultarConductor consultarConductor = new consultarConductor();
                        FragmentTransaction fr = getFragmentManager().beginTransaction();
                        fr.replace(R.id.nav_host_fragment, new consultarConductor());
                        fr.commit();
                        ((Administrador) getActivity()).getSupportActionBar().setTitle("Conductores");

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        if (pDialog.isShowing())
                            pDialog.dismiss();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Ha ocurrido un error, no se ha podido eliminar el documento", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_conductor", id_conductor.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    /*Método EnviarForm() se encaga de enviar al usuario una ventana de confirmación
   para guardar los cambios realizados, al confirmar realizamos una instancia de la clase
   EnviarDatos, esta clase contiene seteados todos los elementos del documento y los devuelve mediante el uso de una Lista*/
    public void EnviarForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta Seguro que desea Modificar el Conductor Actual?");
        myBuild.setIcon(R.drawable.ic_error_outline_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // new Conductor.EnviarDatos(getActivity()).execute();
                cargarWebService();
            }
        });
        myBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBuild.create();
        dialog.show();
    }

    /*En el siguiente método nos conectamos al ws que realiza la consulta de los conductores
    y los obtenemos recorriendo el json que trae todos los datos y luego los alojamos en un ArrayList del tipo
    de la Clase ConsultaConductor que hará uso de todos sus atributos, para traer el solicitado hacemos uso de la variable contenida
    en la Clase ConsultaConductor idConductorAux*/
    public void cargarConductorBuscado(){
          final String URLB = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/ws_buscarConductorId.php";
          RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
          StringRequest stringRequest = new StringRequest(Request.Method.POST, URLB, new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                  response = response.replace("][", ",");
                  if (response.length() > 0) {
                      try {
                          JSONArray bdoc = new JSONArray(response);
                          Log.i("sizejson", "" + bdoc.length());
                          ArrayList<ConsultaConductor> listB = new ArrayList<ConsultaConductor>();
                          for (int i = 0; i < bdoc.length(); i += 10) {
                              try {
                                  listB.add(new ConsultaConductor(
                                          bdoc.getString(i+0 ),
                                          bdoc.getString(i + 1),
                                          bdoc.getString(i + 2),
                                          bdoc.getString(i + 3),
                                          bdoc.getString(i + 4),
                                          bdoc.getString(i + 5),
                                          bdoc.getString(i + 6),
                                          bdoc.getString(i + 7),
                                          bdoc.getString(i + 8),
                                          bdoc.getString(i + 9)));
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                          }
                          cargarCampos(listB);
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                  }
              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

              }
          }) {
              @Override
              protected Map<String, String> getParams() throws AuthFailureError {
                  Map<String, String> parametros = new HashMap<String, String>();
                  String busqueda = ConsultaConductor.getIdConductorAux();
                  parametros.put("campo", busqueda);
                  return parametros;
              }
          };
          requestQueue.add(stringRequest);

    }

    /*Seteamos los valores obtenidos del ArrayList lleno obtenido del método cargarConductorBuscado, lo recorremos y lo asignamos a la propiedad Text de cada EditText*/
    public void cargarCampos(ArrayList list){
        for (int i=0; i<list.size();i++){
            ArrayList<ConsultaConductor> conductor = new ArrayList<ConsultaConductor>();
            conductor = list;
            id_conductor.setText(conductor.get(i).getId_conductor().toString());
            edNombre.setText(conductor.get(i).getNombre().toString());
            edApellido.setText(conductor.get(i).getApellido().toString());
            edTelefono.setText(conductor.get(i).getTelefono().toString());
            edNDUI.setText(conductor.get(i).getDui().toString());
            edNit.setText(conductor.get(i).getNit().toString());
            edNumLicencia.setText(conductor.get(i).getLicencia().toString());
            edDireccion.setText(conductor.get(i).getDireccion().toString());
            id_url.setText(conductor.get(i).getUrl_foto().toString());
            cargarImagen(conductor.get(i).getUrl_foto().toString());

            List<String> lables = new ArrayList<String>();
            for (int j = 0; j < tipoLicenciaList.size(); j++) {
                lables.add(tipoLicenciaList.get(j).getLicencia());
            }
            // Creating adapter for spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_item, lables);
            spinnerTipoLicencia.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();
            spinnerTipoLicencia.setSelection(spinnerAdapter.getPosition(conductor.get(i).getTipo_licencia().toString()));
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    //Método para obtener la posición de un ítem del spinner
    public int obtenerPosicionItem(Spinner spinner, String obtenido) {
        //Creamos la variable posicion y lo inicializamos en 0
        int posicion = 0;
        //Recorre el spinner en busca del ítem que coincida con el parametro String obtenido
        //que lo pasaremos posteriormente
        for (int i = 0; i < spinner.getCount(); i++) {
            //Almacena la posición del ítem que coincida con la búsqueda
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(obtenido)) {
                posicion = i;
            }
        }
        //Devuelve un valor entero (si encontro una coincidencia devuelve la
        // posición 0 o N, de lo contrario devuelve 0 = posición inicial)
        return posicion;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //Método para limpiar después de cada acción realizada
    public void LimpiarElementos(){
        edNombre.setText("");
        edApellido.setText("");
        edTelefono.setText("");
        edNDUI.setText("");
        edNit.setText("");
        edDireccion.setText("");
        edNumLicencia.setText("");
    }


    /*Cargar List de idiomas mediante un hilo secundario AsyncTask*/
    public class getTipoLicencias extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            HandlerService jsonParser = new HandlerService();
            String json = jsonParser.makeServiceCall(URL_TIPOLICENCIA, HandlerService.GET);
            Log.e("Response: ", "> " + json);
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray language = jsonObj
                                .getJSONArray("licencias");

                        for (int i = 0; i < language.length(); i++) {
                            JSONObject idiObj = (JSONObject) language.get(i);
                            Licencia idi = new Licencia (idiObj.getInt("id"),
                                    idiObj.getString("name"));
                            tipoLicenciaList.add(idi);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "No se recibe datos del servidor!");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            cargarConductorBuscado();

            //  populateSpinnerLicencia();
        }
    }

    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }
        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            //error en esta linea
            String authorities=getActivity().getApplicationContext().getPackageName()+".provider";
            Toast.makeText(getActivity().getApplicationContext().getApplicationContext(), authorities, Toast.LENGTH_LONG).show();
            //error en esta linea
            Uri imageUri=    FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", imagen);
          //  Uri imageUri= FileProvider.getUriForFile(getActivity().getApplicationContext().getApplicationContext(),authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==getActivity().RESULT_OK){

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                    imagen.setImageURI(miPath);
                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),miPath);
                        imagen.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });
                    //Bitmap bitmap= BitmapFactory.decodeFile(path);
                    bitmap= BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    break;
            }}}

}
