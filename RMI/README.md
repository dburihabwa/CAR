#   CAR - TP3

01/04/2014

Dorian Burihabwa

##  Introduction
Ce projet vise à l'implémentation d'un système de communication entre des nœuds disposés en arbre ou en graphe s'échangeant des messages par RMI[0].
Bonne lecture.

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

```bash
    mvn -f pom.xml package
    mvn -f pomRegistry.xml package
    mvn -f pomCommander.xml package
```
## Lancer le projet
Les packages générés doivent être lancés dans l'ordre suivant.

* RegistryLauncher
* Node
* Commander

Le RegistryLauncher est lancé par la commande:

```bash
    java -jar RMI-Registry-0.0.1-SNAPSHOT.jar
```


Les nœeuds sont lancés par la commande suivante:
   
```bash
    java -jar RMI-Node-0.0.1-SNAPSHOT.jar <conf.properties>
```

L'interpréteur de commande est lancé par la commande suivante:

```bash
    java -jar RMI-Node-0.0.1-SNAPSHOT.jar <registry host> <registry port> <name>
```

Pour plus de facilité, le script shell launch.sh peut lancer les différents package dans l'ordre.
    
```bash
    sh launch.sh
```

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
site.children = A
```

## Code sample
Le morceau de code suivant présente le traitement appliqué lors de la réception des messages afin d'éviter la répétion des messages.

```java
    
    public boolean receive(final Message message) throws RemoteException {
        if (message == null) {
            throw new IllegalArgumentException(
                    "message argument cannot be null");
        }
        if (this.received.contains(message)) {
            return false;
        }
        if (this.sent.contains(message)) {
            return false;
        }

        logger.log(Level.INFO, "RECEIVED : " + message.getContent() + " (from "
                + message.getSender().getName() + ")");
        Runnable propagator = new Runnable() {
            public void run() {
                try {
                    SiteImpl.this.propagate();
                } catch (RemoteException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    return;
                }
            }
        };

        boolean result = this.received.add(message);
        if (result) {
            Thread t = new Thread(propagator);
            t.start();
        }
        return result;
    }
```

Le morceau de code permet aux nœuds de récupérer périodiquement leurs fils.
De cette manière le nœud pourra récupérer au cours de son existence, les nœuds auxquels il est supposé transmettre ses messages sans forcément les avoir chargé à son démarrage.

```java

    TimerTask task = new TimerTask() {
        @Override
            public void run() {
                String childrenProperty = properties
                    .getProperty("site.children");
                if (childrenProperty == null) {
                    return;
                }
                String[] children = childrenProperty.split(",");
                for (String child : children) {
                    try {
                        SiteItf s = (SiteItf) registry.lookup(child);
                        site.addChild(s);
                    } catch (NotBoundException e) {
                        logger.log(Level.WARNING, "Could not find child "
                                + child);
                    } catch (AccessException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    } catch (RemoteException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
    };
    Timer timer = new Timer();
    int schedule = DEFAULT_TIMER;
    String scheduleProperty = properties.getProperty("site.schedule");
    if (scheduleProperty != null) {
        schedule = Integer.parseInt(scheduleProperty);
    }

timer.scheduleAtFixedRate(task, 0, schedule);

```

##  Conclusion
Ce projet a permis d'aborder l'aspect de la communication pure dans un système réparti.

En se concentrant sur la communication seule, les problématiques d'accès entre nœuds et la prévention de cycles dans la transmission de message ont vite été rencontrées.

De part la multitude des configurations possibles, le développement a du se faire en tenant compte de l'autonomie de chacun des nœuds en abordant une attitude défensive (vérification de la présence des fils, validation des messages, ...).

Une attitude défensive qui a mené au développement d'une solution économe et efficace.

Merci pour votre lecture.


## Références
[0] RMI -http://www.oracle.com/technetwork/java/javase/tech/index-jsp-136424.html 
