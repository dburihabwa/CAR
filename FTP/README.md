#   Conception d'Applications Réparties
##  TP1 : Serveur FTP
Dorian BURIHABWA
17/02/2014

##  Introduction
Ce projet vise à implémenter un serveur FTP répondant aux commandes suivantes:
* USER
* PASS
* RETR
* STOR
* LIST
* QUIT
* PWD
* CWD
* CDUP

##  Architecture
Le code de l'application se divise en 3 (sous-) packages:
* ftp
* ftp.exceptions
* ftp.utils

### FTP
Le package FTP contient les classes de bases nécessaires au bon fonctionnement du serveur:
####    Main
Classe permettant l'éxecution de l'application.
####    Server
Classe recevant les connections clients
####    ClientSession
Classe conservant les informations du client au cours de sa connexion et ses échanges avec l'application.
####    FtpRequest
Classe traitant les commandes envoyées par le client
####    Commannd
Classe listant les commandes autorisées et prises en charges par le serveur.

### EXCEPTIONS
Le package ftp.exceptions contient les exceptions développées pour ce projet.
####    UnsupportedCommandException
Exception décrivant une erreur à l'interprétation de la commande.

### UTILS
Ce package contient des classes utilitaires.
####    CommandParser
Classe parsant les commandes reçues sous forme de chaine de caractères.
####    FTPWorker
Classe permettant de recevoir les commandes du client.
Son code peut être éxécuté dans un Thread différent de celui recevant les connexions.

### Gestion des erreurs
TODO : Expliquer la gestion des erreurs

## Code samples
TODO : Placer le code de processRequest  