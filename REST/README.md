#   CAR - TP2
Dorian Burihabwa

##  Introduction
Ce projet vise à l'implémentation d'une passerelle REST permettant l'interaction avec un serveur FTP.
##  Structure
Afin de répondre au problème, l'API est découpée en 2 parties distinctes (package api):
* Directory (DirResource.java)
* File (FileResource.java)

La communication avec le serveur FTP se fait à l'aide d'un FTPAdapter (package utils) implémenté par FTPAdapterImpl fournissant toutes les opérations nécessaires à l'intéraction avec le serveur.

La manipulation des variables concernant l'authentification est déléguée à la classe BasicAuthentication.

##  Fonctionnalités implémentées
La communication avec la passerelle se fait par Basic HTTP authentication. Dans le cas où l'utilisateur n'est pas connecté, il reçoit un message d'erreur l'invitant à se connecter.
La navigation n'étant pas complète, il est recommandé d'utiliser curl afin de tirer parti de l'API dévelopée.
Les fonctionnalités implémentées sont les suivantes.

### Récupération de fichier 
``GET REST/api/file/myfile``
Permet de récupérer un fichier en donnant son chemin depuis la racine du server.

### Modification de fichier
``POST REST/api/file/myfile``
Permet de modifier un fichier en donnant son chemin depuis la racine du server.
Cette méthode visant à la modification, elle ne modifiera le fichier que si elle ne peut trouver un fichier portant ce nom sur le serveur.
Dans le cas ou elle n'en trouvait pas, elle renverrait un message d'erreur.

### Upload de fichier
``PUT REST/api/file/myfile``
Permet d'ajouter un fichier sur le serveur.

### Effacement de fichier
``DELETE REST/api/file/myfile``
Permet d'effacer un fichier sur le serveur.

### Listing de répertoire
``GET REST/api/dir/mydir``
Permet de lister le contenu d'un répertoire sur le serveur.

### Création de répertoire
``PUT REST/api/dir/mydir``
Permet de créer un répertoire sur le serveur.

### Effacement de  répertoire
``DELETE REST/api/dir/mydir``
Permet d'éffacer un répertoire non vide sur le serveur.

## Code sample
Pour information, voici un morceau de code permettant l'upload de fichier.
'''
public void stor(String path, InputStream received) throws IOException {
    if (path == null) {
        throw new IllegalArgumentException("path argument cannot be null!");
    }
    if (received == null) {
        throw new IllegalArgumentException("received argument cannot be null!");
    }
    if (!this.client.isConnected()) {
        authenticate();
    }
    String parentDirectory = getParentDirectory(path);
    String file = getFile(path);
    this.client.changeWorkingDirectory(parentDirectory);
    this.client.setBufferSize(4096);
    this.client.setFileType(FTP.BINARY_FILE_TYPE);
    this.client.setFileTransferMode(FTP.COMPRESSED_TRANSFER_MODE);
    boolean storeFile = this.client.storeFile(file, received);
    if (!storeFile) {
        Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.WARNING, this.client.getReplyString());
        throw new IOException(this.client.getReplyString());
    }
    received.close();
    close();
}
'''
Ce code est défini dans l'implémenation du FTPAdapter, FTPAdapterImpl et est utilisé à par les méthodes POST et PUT sur l'API file.
Commençant par vérifier les arguments pour détecter les erreurs abhérantes, le code se poursuit par l'utilisation des méthodes utilitaires getFile (retourant la dernière partie du chemin soit le nom du fichier) et getParentDirectory (retournant le début du chemin soit le répertoire où créer le fichier).
La copie du fichier est ensuite entamée. Suite à quoi le flux de fichier et la connexion au serveur FTP sont fermés.

##  Conclusion
Ce projet a révélé ses difficultés dès le départ en offrant un challenge quant à sa configuration et l'ajout de dépendances pour courvrir la pauvreté de l'API web services de JAVA.
Il a revanche permis de revenir rapidement sur les limites du serveur FTP implémenté au premier TP.
De plus la technologie étant plus récente et mieux entretenue, trouver de la documentation ne fut pas une grande difficulté.

En conclusion, ce projet s'est avérér un véritable challenge au bout que seules une bonne documentation et où une première expérience du développement web en java apporte permette d'accélérer.
