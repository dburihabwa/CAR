#   CAR - TP3

01/04/2014

Dorian Burihabwa

##  Introduction
Ce projet vise à l'implémentation d'un système de communication entre des nœuds disposés en arbre ou en graphe s'échangeant des messages par RMI[0].

##  Structure
L'implémentation est séparée en 3 packages:

### rmi
Package contenant les classes exécutables:

* Main : pour les nœuds
* RegistryLauncher: lance le RMI registry
* Commander: prompt permettant la communication avec les nœuds

### rmi.interfaces
Package contenant les interfaces étendant Remote qui seront échangés entre les différents programmes.

### rmi.impl
Package contenant la classe Prompt permettant d'envoyer des messages à un nœud.

## Compiler le projet
Les exécutables nécessaires pour utiliser ce système peuvent être obtenus en générant 3 archives jars à partir du projet.
Ces archives seront lancées avec les main class suivantes:

* fr.lille1.car.rmi.Main
* fr.lille1.car.rmi.RegistryLauncher
* fr.lille1.car.rmi.Commander

Pour plus de facilité, ces packages peuvent être générés à l'aide de commandes maven:

    mvn -f pom.xml package
    mvn -f pomRegistry.xml package
    mvn -f pomCaommander.xml package
## Lancer le projet
Les packages générés doivent être lancés dans l'ordre suivant.

* RegistryLauncher
* Node
* Commander

Le RegistryLauncher est lancé par la commande:

    java -jar RMI-Registry-0.0.1-SNAPSHOT.jar


Les nœeuds sont lancés par la commande suivante:
   
    java -jar RMI-Node-0.0.1-SNAPSHOT.jar <conf.properties>

L'interpréteur de commande est lancé par la commande suivante:

    java -jar RMI-Node-0.0.1-SNAPSHOT.jar <registry host> <registry port> <name>

Pour plus de facilité, le script shell launch.sh peut lancer les différents package dans l'ordre.
    
    sh launch.sh

##  Fonctionnalités implémentées
### Communication en arbre
Chaque nœud lit sa configuration dans le fichier passé en argument sur la ligne de commande afin de connaitre:

* son nom (site.name)
* le nom de ses fils (site.children)
* l'adresse et le port du RMI registry (registry.host et registry.port)
* la fréquence de consultation du registry pour récupérer ses fils (site.schedule)

Ses paramètres lui permettent de récupérer ses fils et de leur communiquer les messages reçus.

### Répétition des messages
La répétition des messages est évitée grâce à l'usage d'une structure de message retenant:

* L'émetteur du message
* Le contenu du message
* La date de création du message

Chaque noeud dispose d'un ensemble de messages reçus et de messages envoyés. À chaque réception de nouveaux messages, il vérifie qu'il n'a pas déjà transmis ou reçu le message et choisit en conséquence d'envoyer ou de ne pas transmettre le message.

### Communication en graphe
Les nœuds peuvent être disposés sous forme de graphe pour leur communication.
Afin de réaliser un graphe orienté, il suffit d'ajouter de nouveaux fils à la liste des fils(site.children).
Dans le cas d'un graphe non-orienté, les relations entre le nœud et ses fils doivent être décrites dans les fichiers de configuration de chacun des nœuds pour représenter la bi-directonalité.

```
A <-> B

# conf de A
site.children = B

# conf de B
site.children = B
```

## Code sample

##  Conclusion

## Références
[0] RMI -http://www.oracle.com/technetwork/java/javase/tech/index-jsp-136424.html 
