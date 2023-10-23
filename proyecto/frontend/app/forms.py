from flask_wtf import FlaskForm
from wtforms import (StringField, PasswordField, BooleanField, FileField, SubmitField)
from wtforms.validators import InputRequired, Length, Email, EqualTo

class LoginForm(FlaskForm):
    email = StringField('email', validators=[Email()])
    password = PasswordField('password', validators=[InputRequired()])
    remember_me = BooleanField('remember_me')
    submit = SubmitField('Sign in')

# clase definida para el formulario de registro
class RegistrationForm(FlaskForm):
    email = StringField('email', validators=[Email()])
    name = StringField('name', validators=[InputRequired()])
    password = PasswordField('password', validators=[InputRequired()])
    submit = SubmitField('Sign Up')

# clase definida para el formulario de la base de datos
class DatabaseForm(FlaskForm):
    name = StringField('name', validators=[InputRequired()])
    key = StringField('key', validators=[InputRequired()])
    value = StringField('value', validators=[InputRequired()])
    submit = SubmitField('Create database')

# clase definida para añadir claves en la base de datos
class AddKeyValueForm(FlaskForm):
    key = StringField('key', validators=[InputRequired()])
    value = StringField('value', validators=[InputRequired()])
    submit = SubmitField('Add')

class RemoveKeyValueForm(FlaskForm):
    key = StringField('key', validators=[InputRequired()])
    submit = SubmitField('Remove')

class ProcessMRForm(FlaskForm):
    funcion = StringField('funcion', validators=[InputRequired()])
    submit = SubmitField('Process')