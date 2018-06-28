package com.tecnologiaparainmobiliarias.centrodenotificacionestpi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.ActualizarToken;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.ApiError;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.UsuarioLogin;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias.PreferenciasUsuario;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginTPIActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mImageView;

    private EditText mPasswordView;
    private EditText mCuentaView;
    private TextView mUserView;

    private TextInputLayout mFloatLabelUser;
    private TextInputLayout mFloatLabelPassword;
    private TextInputLayout mFloatLabelCuenta;


    private Button btnIniciar;
    private LinearLayout mAccesoRapido;

    private RelativeLayout mContentReintentar;
    private TextView mTxtMensajeErrorInicio;
    private Button mBtnCancelar;
    private Button mBtnReintentar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tpi);
        // Set up the login form.
        mContentReintentar = (RelativeLayout) findViewById(R.id.contenedorReintentar);
        mBtnCancelar = (Button) findViewById(R.id.btnCancelar);
        mBtnReintentar = (Button) findViewById(R.id.btnReintentar);

        mContentReintentar.setVisibility(View.GONE);
        mBtnCancelar.setVisibility(View.GONE);
        mBtnReintentar.setVisibility(View.GONE);

        mTxtMensajeErrorInicio = (TextView) findViewById(R.id.txtMensajeErrorInicio);

        //Crear conexion REST
        mRestAdaper = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        //Crear conexion
        mCrmApi = mRestAdaper.create(CrmApi.class);

        mImageView = (ImageView) findViewById(R.id.image_logo);
        mUserView = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        mCuentaView = (EditText) findViewById(R.id.cuenta);

        mFloatLabelCuenta = (TextInputLayout) findViewById(R.id.float_label_cuenta);
        mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);
        mFloatLabelUser = (TextInputLayout) findViewById(R.id.float_label_user);

        Button mIniciarSesion = (Button) findViewById(R.id.iniciar_sesion);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                /*if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }*/
                if(id == R.id.login || id == EditorInfo.IME_NULL){
                    if(!isOnline()){
                        showLoginError("Conexion de conexion, red no disonible");
                        return false;
                    }
                    //attemptLogin();
                    ComprobarSesionIniciada();
                    return true;
                }
                return false;
            }
        });


        mIniciarSesion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOnline()){
                    showLoginError("Error de conexion, red no disponible");
                }
                //attemptLogin();
                ComprobarSesionIniciada();
            }
        });


        Bundle datos = LoginTPIActivity.this.getIntent().getExtras();
        String estado_sesion_actual = "";
        if(datos != null){
             estado_sesion_actual = datos.getString("estado_sesion");
        }

        //String fecha_ultimo_ingreso = datos.getString("fecha_ingreso");
        //String medio_ingreso = datos.getString("medio");

        //String estado_sesion_actual = getIntent().getStringExtra("estado_sesion");
        Log.d("EstadoBundle","- "+estado_sesion_actual);

        if(estado_sesion_actual.equals("SESSION_INICIADA_EN_OTRO_DISPOSITIVO")){
            mLoginFormView.setVisibility(View.GONE);
            mContentReintentar.setVisibility(View.VISIBLE);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha = null;
            try {
                fecha = format.parse(datos.get("fecha_ultimo_ingreso").toString());

            } catch (ParseException e) {
                Log.d("FechaIngreso",e.getMessage());
                e.printStackTrace();
            }

            format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm aaa", new Locale("ES"));
            String fechaIngreso = "";

            fechaIngreso = format.format(fecha);


            String MensajeError = "Se a detectado una sesion activa en otro dispositivo.";
            if (datos.get("tipo_accion").equals("VERIFICACION_SESION")){
                mBtnCancelar.setText("Aceptar");
                MensajeError += " Y se ha cerrado la sesion en este dispositivo";
            }

            if(datos.get("medio_ingreso").toString().equals("APP_ANDROID")){
                MensajeError += "\n\nEl dispositivo actual es un Android ";

                if(fecha != null){
                    MensajeError += " e inicio sesion el "+fechaIngreso;
                }
            }
            if(datos.get("medio_ingreso").toString().equals("APP_IOS")){
                MensajeError += " El dispositivo actual es un iOS";
                if(fecha != null){
                    MensajeError += " con inició de sesión el "+fechaIngreso;
                }
            }

            if (datos.get("tipo_accion").equals("INICIO_SESION")){
                MensajeError += " \n\nSi continua se cerrara la sesion en el disposivo anterior y se establecera este como principal.";
            }

            if (datos.get("tipo_accion").equals("VERIFICACION_SESION")){
                //mBtnCancelar.setText("Aceptar");
                MensajeError += "\n\nVuelva a iniciar sesion para establecer a este dispocitivo como principal.";
            }


            mTxtMensajeErrorInicio.setText(MensajeError);
            mTxtMensajeErrorInicio.setTextSize(16);
            mTxtMensajeErrorInicio.setVisibility(View.VISIBLE);
            mBtnCancelar.setVisibility(View.VISIBLE);

            if(datos.get("tipo_accion").equals("INICIO_SESION")){
                mBtnReintentar.setText("Continuar");
                mBtnReintentar.setVisibility(View.VISIBLE);
                mLoginFormView.setVisibility(View.GONE);
            }


        }



        //Si ya ha iniciado sesion anteriormente
        SharedPreferences mPref = getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
        String claveSession = mPref.getString("PREF_CLAVE_SESION", "");
        String idusuario = mPref.getString("PREF_ID", "");
        String cuenta = mPref.getString("PREF_CUENTA", "");
        String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(!claveSession.isEmpty() && !idusuario.isEmpty() && !cuenta.isEmpty()){
            //Toast.makeText(this,"Sesion iniciada anteriormente",Toast.LENGTH_LONG).show();
            mContentReintentar.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);


        }



        mBtnCancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenciasUsuario.get(LoginTPIActivity.this).LogOut();
                mContentReintentar.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
            }
        });

        mBtnReintentar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentReintentar.setVisibility(View.GONE);
                IniciarSesion(mCuentaView.getText().toString(),mUserView.getText().toString(),mPasswordView.getText().toString());
            }
        });

    }

    private void ComprobarSesionIniciada(){
        // Reset errors.
        mFloatLabelCuenta.setError(null);
        mFloatLabelUser.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        final String user = mUserView.getText().toString();
        final String cuenta = mCuentaView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)){
            mFloatLabelPassword.setError("Este campo es requerido");
            focusView = mFloatLabelPassword;
            cancel = true;
        }else if (!isPasswordValid(password) ){
            mFloatLabelPassword.setError("Contraseña invalida");
            focusView = mFloatLabelPassword;
            cancel = true;
        }


        // Check for a valid user.
        if(TextUtils.isEmpty(user)){
            mFloatLabelUser.setError("Este campo es requerido");
            focusView = mFloatLabelUser;
            cancel = true;
        }

        // Check for a valid cuenta
        if(TextUtils.isEmpty(cuenta)){
            mFloatLabelCuenta.setError("Este campo es requerido");
            focusView = mFloatLabelCuenta;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            mProgressView.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);

            final String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            Call<VerificarSesion> loginCall = mCrmApi.verificarSesionInicioSesion(cuenta,user,password,"APP_ANDROID",iddispositivo);

            loginCall.enqueue(new Callback<VerificarSesion>() {
                @Override
                public void onResponse(Call<VerificarSesion> call, Response<VerificarSesion> response) {

                    //Quitar progreso
                    mProgressView.setVisibility(View.GONE);


                    //Procesar Errores
                    if(!response.isSuccessful()){
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if(response.errorBody().contentType().subtype().equals("aplication/json")){
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                            error = apiError.getEstado();

                        }else{
                            error = response.message();
                        }
                        //Log.d("ERROR_RETROFIT", error);
                        showLoginError(error);
                        return;
                    }


                    String EstadoSession = response.body().getEstado().toString();

                    //Log.d("EstadoSession",EstadoSession);

                    if(EstadoSession.equals("SESSION_AUTORIZADA")){
                        IniciarSesion(cuenta, user, password);
                    }else{

                        if(EstadoSession.equals("ERROR_CUENTA_USUARIO_SUSPENDIDA")){

                            showDialogMessageSimple("Su cuenta de susario se encuentra actualmente suspendida, contacte con su administrador para resolver el problema.");

                        }else if(EstadoSession.equals("ERROR_NO_MOVIL")){

                            showDialogMessageSimple("Su cuenta es incompatible con esta versión de la aplicación.");

                        }else if(EstadoSession.equals("ERROR_DATOS_INCORRECTOS")){
                            showDialogMessageSimple("Los datos de acceso no son correctos, verifique y vuelva a intentar.");
                        }else if(EstadoSession.equals("SESSION_INICIADA_EN_OTRO_DISPOSITIVO")){

                            mLoginFormView.setVisibility(View.GONE);
                            mContentReintentar.setVisibility(View.VISIBLE);


                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date fecha = null;
                            try {
                                fecha = format.parse(response.body().getFecha_ingreso());

                            } catch (ParseException e) {
                                //Log.d("FechaIngreso",e.getMessage());
                                e.printStackTrace();
                            }

                            format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a la(s)' hh:mm aaa", new Locale("ES"));
                            String fechaIngreso = "";

                            fechaIngreso = format.format(fecha);


                            String MensajeError = "Se a detectado una sesion activa en otro dispositivo.";

                            if(response.body().getMedio().toString().equals("APP_ANDROID")){
                                MensajeError += " con sistema operativo android ";

                                if(fecha != null){
                                    MensajeError += " con inicio de sesion el "+fechaIngreso;
                                }
                            }
                            if(response.body().getMedio().toString().equals("APP_IOS")){
                                MensajeError += " con sistema operativo iOS";
                                if(fecha != null){
                                    MensajeError += " con inicio de sesion el "+fechaIngreso;
                                }
                            }

                            MensajeError += " \n\nSi continua se Cerrara la sesion en el disposivo anterior y se establecera este como principal.";

                            mTxtMensajeErrorInicio.setText(MensajeError);
                            mTxtMensajeErrorInicio.setTextSize(16);
                            mTxtMensajeErrorInicio.setVisibility(View.VISIBLE);
                            mBtnCancelar.setVisibility(View.VISIBLE);

                            //if(datos.get("tipo_accion").equals("INICIO_SESION")){
                                mBtnReintentar.setText("Continuar");
                                mBtnReintentar.setVisibility(View.VISIBLE);
                            //}
                        }else{
                            Toast.makeText(LoginTPIActivity.this,"Ocurrio un error al iniciar sesion, intente de nuevo.",Toast.LENGTH_LONG).show();
                            return;
                        }

                    }


                }

                @Override
                public void onFailure(Call<VerificarSesion> call, Throwable t) {
                    showProgress(false);
                    Log.d("Error_retrofit_login",t.getMessage()+" "+t.getLocalizedMessage());
                }
            });
        }
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mFloatLabelCuenta.setError(null);
        mFloatLabelUser.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        final String cuenta = mCuentaView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)){
            mFloatLabelPassword.setError("Este campo es requerido");
            focusView = mFloatLabelPassword;
            cancel = true;
        }else if (!isPasswordValid(password) ){
            mFloatLabelPassword.setError("Contraseña invalida");
            focusView = mFloatLabelPassword;
            cancel = true;
        }


        // Check for a valid user.
        if(TextUtils.isEmpty(user)){
            mFloatLabelUser.setError("Este campo es requerido");
            focusView = mFloatLabelUser;
            cancel = true;
        }

        // Check for a valid cuenta
        if(TextUtils.isEmpty(cuenta)){
            mFloatLabelCuenta.setError("Este campo es requerido");
            focusView = mFloatLabelCuenta;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            final String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            Call<UsuarioLogin> loginCall = mCrmApi.login(cuenta,user,password,"APP_ANDROID",iddispositivo);

            loginCall.enqueue(new Callback<UsuarioLogin>() {
                @Override
                public void onResponse(Call<UsuarioLogin> call, Response<UsuarioLogin> response) {

                    //Quitar progreso
                    showProgress(false);

                    //Procesar Errores
                    if(!response.isSuccessful()){
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if(response.errorBody().contentType().subtype().equals("aplication/json")){
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                            error = apiError.getEstado();
                            //Log.d("LoginTPIActivity",apiError.getEstado());
                        }else{
                            error = response.message();
                        }
                        //Log.d("ERROR_RETROFIT", error);
                        showLoginError(error);
                        return;
                    }


                    String EstadoSession = response.body().getEstado().toString();

                    if(EstadoSession.equals("SESSION_AUTORIZADA") && response.body().getClave_session() != null){
                        Toast.makeText(LoginTPIActivity.this,"Sesion Iniciada", Toast.LENGTH_LONG).show();

                        response.body().setPassword(password);

                        //Guardar preferencias de usuario
                        PreferenciasUsuario.get(LoginTPIActivity.this).guardarUsuario(response.body());

                        //Enviar token de notificacion al servidor
                        String fcmToken = FirebaseInstanceId.getInstance().getToken();
                        String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        //Log.d("DATA_TOKEN", "data: "+cuenta+"-"+response.body().getId()+"-"+fcmToken+"-"+response.body().getClave_session()+"-ANDROID-"+iddispositivo);

                        Call<ActualizarToken> registrarToken = mCrmApi.actualizarToken(cuenta, response.body().getId().toString(),fcmToken,response.body().getClave_session().toString(),"ANDROID",iddispositivo);

                        registrarToken.enqueue(new Callback<ActualizarToken>() {
                            @Override
                            public void onResponse(Call<ActualizarToken> call, Response<ActualizarToken> responseT) {
                                if(!responseT.isSuccessful()){
                                    String error = "Ha ocurrido un error. Contacte al administrador.";
                                    if(responseT.errorBody().contentType().subtype().equals("aplication/json")){
                                        ApiError apiError = ApiError.fromResponseBody(responseT.errorBody());

                                        error = apiError.getEstado();
                                        //Log.d("LoginCRMActivity",apiError.getEstado());
                                    }else{
                                        error = responseT.message();
                                    }
                                    //Log.d("ERROR_RETROFIT",error);
                                }
                            }

                            @Override
                            public void onFailure(Call<ActualizarToken> call, Throwable tt) {
                                Log.d("Error_retrofit_token",tt.getMessage()+" "+tt.getLocalizedMessage());
                            }
                        });

                        showAppointmentsScreen();
                    }else{

                        if(EstadoSession.equals("ERROR_CUENTA_USUARIO_SUSPENDIDA")){

                            showDialogMessageSimple("Su cuenta de susario se encuentra actualmente suspendida, contacte con su administrador para resolver el problema.");

                        }else if(EstadoSession.equals("ERROR_NO_MOVIL")){

                            showDialogMessageSimple("Su cuenta es incompatible con esta versión de la aplicación.");

                        }else if(EstadoSession.equals("ERROR_DATOS_INCORRECTOS")){
                            showDialogMessageSimple("Los datos de acceso no son correctos, verifique y vuelva a intentar.");
                        }else{
                            Toast.makeText(LoginTPIActivity.this,"Ocurrio un error al iniciar sesion, intente de nuevo.",Toast.LENGTH_LONG).show();
                            return;
                        }

                    }


                }

                @Override
                public void onFailure(Call<UsuarioLogin> call, Throwable t) {
                    showProgress(false);
                    Log.d("Error_retrofit_login",t.getMessage()+" "+t.getLocalizedMessage());
                }
            });
        }
    }

    private void IniciarSesion(final String cuenta, final String user, final String password) {
        // Reset errors.

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);

        final String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<UsuarioLogin> loginCall = mCrmApi.login(cuenta,user,password,"APP_ANDROID",iddispositivo);

        loginCall.enqueue(new Callback<UsuarioLogin>() {
            @Override
            public void onResponse(Call<UsuarioLogin> call, Response<UsuarioLogin> response) {

                //Quitar progreso
                showProgress(false);

                //Procesar Errores
                if(!response.isSuccessful()){
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if(response.errorBody().contentType().subtype().equals("aplication/json")){
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        error = apiError.getEstado();
                        //Log.d("LoginTPIActivity",apiError.getEstado());
                    }else{
                        error = response.message();
                    }
                    //Log.d("ERROR_RETROFIT", error);
                    showLoginError(error);
                    return;
                }


                String EstadoSession = response.body().getEstado().toString();

                if(EstadoSession.equals("SESSION_AUTORIZADA") && response.body().getClave_session() != null){
                    Toast.makeText(LoginTPIActivity.this,"Sesion Iniciada", Toast.LENGTH_LONG).show();

                    response.body().setPassword(password);

                    //Guardar preferencias de usuario
                    PreferenciasUsuario.get(LoginTPIActivity.this).guardarUsuario(response.body());

                    //Enviar token de notificacion al servidor
                    String fcmToken = FirebaseInstanceId.getInstance().getToken();
                    String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    //Log.d("DATA_TOKEN", "data: "+cuenta+"-"+response.body().getId()+"-"+fcmToken+"-"+response.body().getClave_session()+"-ANDROID-"+iddispositivo);

                    Call<ActualizarToken> registrarToken = mCrmApi.actualizarToken(cuenta, response.body().getId().toString(),fcmToken,response.body().getClave_session().toString(),"ANDROID",iddispositivo);

                    registrarToken.enqueue(new Callback<ActualizarToken>() {
                        @Override
                        public void onResponse(Call<ActualizarToken> call, Response<ActualizarToken> responseT) {
                            if(!responseT.isSuccessful()){
                                String error = "Ha ocurrido un error. Contacte al administrador.";
                                if(responseT.errorBody().contentType().subtype().equals("aplication/json")){
                                    ApiError apiError = ApiError.fromResponseBody(responseT.errorBody());

                                    error = apiError.getEstado();
                                    //Log.d("LoginCRMActivity",apiError.getEstado());
                                }else{
                                    error = responseT.message();
                                }
                                //Log.d("ERROR_RETROFIT",error);
                            }
                        }

                        @Override
                        public void onFailure(Call<ActualizarToken> call, Throwable tt) {
                            Log.d("Error_retrofit_token",tt.getMessage()+" "+tt.getLocalizedMessage());
                        }
                    });

                    showAppointmentsScreen();
                }else{

                    if(EstadoSession.equals("ERROR_CUENTA_USUARIO_SUSPENDIDA")){

                        showDialogMessageSimple("Su cuenta de susario se encuentra actualmente suspendida, contacte con su administrador para resolver el problema.");

                    }else if(EstadoSession.equals("ERROR_NO_MOVIL")){

                        showDialogMessageSimple("Su cuenta es incompatible con esta versión de la aplicación.");

                    }else if(EstadoSession.equals("ERROR_DATOS_INCORRECTOS")){
                        showDialogMessageSimple("Los datos de acceso no son correctos, verifique y vuelva a intentar.");
                    }else{
                        Toast.makeText(LoginTPIActivity.this,"Ocurrio un error al iniciar sesion, intente de nuevo.",Toast.LENGTH_LONG).show();
                        return;
                    }

                }

            }

            @Override
            public void onFailure(Call<UsuarioLogin> call, Throwable t) {
                showProgress(false);
                Log.d("Error_retrofit_login2",t.getMessage()+" "+t.getLocalizedMessage());
            }
        });

    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




    private void showAppointmentsScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showDialogMessageSimple(String mensaje){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar",null);
        builder.create();
        builder.show();

    }
}

