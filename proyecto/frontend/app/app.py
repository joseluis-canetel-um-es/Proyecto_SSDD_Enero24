from hashlib import sha512
from flask import Flask, render_template, send_from_directory, url_for, request, redirect
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import requests
import os

# Usuarios
from models import users, User

# Login
from forms import LoginForm, RegistrationForm, DatabaseForm, AddKeyValueForm, RemoveKeyValueForm, ProcessMRForm

import hashlib
import logging
import json

app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app) # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'

@app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('static', path)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    else:
        error = None
        form = LoginForm(None if request.method != 'POST' else request.form)
        if request.method == "POST" and form.validate():

            email = form.email.data
            password = form.password.data
            credenciales = {"email": email, "password": hashlib.sha256( password.encode('utf-8')).hexdigest()}
            cabecera = {"Content-Type" : "application/json"}

            response = requests.post('http://backend-rest:8080/Service/checkLogin', headers=cabecera,json=credenciales)

            if response.status_code == 200: 
                user = User(response.json()['id'], response.json()['name'], form.email.data.encode('utf-8'), form.password.data.encode('utf-8'),response.json()['token'], int(response.json()['visits']))
                users.append(user)
                logging.debug('EEEEEEEEEEEEEEEEEEEH ESTE ES EL NUMERO DE VISITAS', int(response.json()['visits']))
                logging.debug('EEEEEEEEEEEEEEEEEEEH ESTE ES EL NUMERO DE VISITAS EN USER', user.visits)

                login_user(user, remember=form.remember_me.data)
                return redirect(url_for('profile'))
            else:
                error = 'Email o contraseña incorrectos'

        return render_template('login.html', form=form,  error=error)
    

# nueva funcion para permitir registro de usuario
@app.route('/signup', methods=['GET', 'POST'])
def signup():
    logging.basicConfig(level=logging.DEBUG)
    if current_user.is_authenticated:
        return redirect(url_for('index')) #si ya hay user 
    else:
        error = None
        form = RegistrationForm(None if request.method != 'POST' else request.form)
        if request.method == "POST" and form.validate():
            email = form.email.data
            name = form.name.data
            password = form.password.data

            cabecera = {"Content-Type" : "application/json"}
            credenciales_registro = {"email" : email, "name" : name, "password" : hashlib.sha256( password.encode('utf-8')).hexdigest()}
            response = requests.post('http://backend-rest:8080/Service/checkSignup', headers = cabecera, json=credenciales_registro)
            if response.status_code == 200:
                return redirect(url_for('login')) 
            elif response.status_code == 409:
                error = "Email ya registrado"
            else:
                error = 'Validación de registro incorrecta'
    return render_template('signup.html', form=form, error=error)


@app.route('/profile')
@login_required
def profile():
    return render_template('profile.html')

@app.route('/createDatabases', methods=['GET', 'POST'])
@login_required
def createDatabases():
    error = None
    form = DatabaseForm(None if request.method != 'POST' else request.form)
    if request.method == "POST" and form.validate():
        id = current_user.id
        name = form.name.data
        key = form.key.data
        value = form.value.data

        cabecera = {"Content-Type" : "application/json"}
        datos_database = {"name" : name, "key" : key, "value": value}
        response = requests.post('http://backend-rest:8080/Service/u/'+id+'/db', headers = cabecera, json=datos_database)
        if response.status_code == 201:
            error =  "Database registrada correctamente"
        elif response.status_code == 400:
            error = 'No se ha podido crear la base de datos'
        else:
            error = 'Error no controlado'
    return render_template('createDatabases.html', form=form, error=error)

@app.route('/viewDatabases', methods=['GET'])
@login_required
def viewDatabases():
    id = current_user.id
    try:
        response = requests.get('http://backend-rest:8080/Service/u/'+id+'/dbinfo')
    except:
        error = 'No se ha podido hacer la conexion'
    
    if response.status_code == 200:
        databases = response.json()
        return render_template('viewDatabases.html', databases = databases)

    elif response.status_code == 204:
        databases = []
        return render_template('viewDatabases.html', databases = databases)
    else:
        error = 'No se ha podido obtener las bases de datos'
    return render_template('viewDatabases.html', error = error)    

@app.route('/databaseInfo', methods=['GET', 'POST'])
def databaseInfo():
    # -- modificado -- 
    error1 = None
    error2 = None
    error3 = None
    form1 = AddKeyValueForm()
    form2 = RemoveKeyValueForm()
    form3 = ProcessMRForm()
    db_id = request.args.get('db_id')
    id = current_user.id

    if request.method == 'POST':
        if form1.validate_on_submit():
            key = form1.key.data
            value = form1.value.data
            response = requests.put('http://backend-rest:8080/Service/u/'+id+'/db/'+db_id+'/d/'+key+'?v='+value)
            if response.status_code == 200:
                return redirect(url_for('databaseInfo', db_id=db_id))
            elif response.status_code == 400:
                error1 = 'No se ha podido crear la base de datos'
        elif form2.validate_on_submit():
            key = form2.key.data
            response = requests.delete('http://backend-rest:8080/Service/u/'+id+'/db/'+db_id+'/d/'+key)
            if response.status_code == 200:
                return redirect(url_for('databaseInfo', db_id=db_id))
            elif response.status_code == 400:
                error2 = 'No se ha podido crear la base de datos'
        elif form3.validate_on_submit():
            map = form3.map.data
            reduce = form3.reduce.data
            datos_database_mr = {"map" : map, "reduce" : reduce}
            response = requests.put('http://backend-rest:8080/Service/u/'+id+'/db/'+db_id+'/mr',json=datos_database_mr)
            if response.status_code == 202:
                return redirect(url_for('databaseInfo', db_id=db_id))
            elif response.status_code == 400:
                error3 = 'No se ha podido realizar el procesamiento Map reduce'

    response = requests.get('http://backend-rest:8080/Service/u/'+id+'/db/'+db_id)
    if response.status_code == 200:
        database = response.json()
    else:
        pass
    return render_template('databaseInfo.html', database=database, db_id=db_id, form1=form1, form2=form2, form3=form3, error1=error1, error2=error2, error3=error3)

@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))

@login_manager.user_loader
def load_user(user_id):
    for user in users:
        if user.id == user_id:
            return user
    return None

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')