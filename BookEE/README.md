#   CAR - TP4

22/04/2014

Dorian Burihabwa

##  Introduction
Ce projet vise à l'implémentation d'une application web de commande de livre tirant partie de l'environnement Java EE.

##  Structure
L'implémentation est séparée en 2 projets:

* BookEE-ejb : contenant les objets business utilisés lors de l'ajout et de la commande de livres.
* BookEE-war : contenant les servlets et jsp utilisés pour l'application web

### BookEE-ejb
Ce projet contient les deux objets fondamentaux dans la gestion du site de commande:

* Book : livre représenté par un titre unique, un auteur et une année de publication
* Basket : un id unique et un ensemble de livres choisis par le client
Implémenté sous forme d'entités, ces classes fournissent les getters et setters nécessaires à leur utilisation dans le système.

Afin de manipuler les livres et paniers en base de données, la couche de persistance est masquée par l'implémentation de 2 EJB stateless

* BookDAO :  gérant les instances de Book en base de données
* BasketDAO : gérant les instances de Basket en base de données

Ces classes serviront d'intermidiaire vers la base de données à tout autre couche qui souhaiterait utiliser les fonctions liées à la couche de persistance.

### BookEE-war
Ce projet contient les servlets répondant aux requêtes d'un navigateur. Le travail de rendu est divisé entre les servlets et les templates JSP.
La logique à proprement parler se trouvant dans les servlets, les templates ne se contentent uniquement d'en afficher le résultat.
NB: Le déploiement de ce projet nécessite d'embarquer la dépendance BookEE-ejb dans l'archive WAR.

Les différentes servlets développées pour ce projet sont les suivantes:

+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| SERVLET                 | PATH                    |                                                                                              |
+=========================+=========================+==============================================================================================+
| AdminServlet.java       | /admin                  | Permet l'accès au panel d'administration (sans login)                                        |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| AuthorServlet.java      | /authors                | Permet de lister les différents auteurs ayant des livres enregistrés dans la base de données |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| BasketServlet.java      | /basket                 | Permet de visualiser le contenu du panier                                                    |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| BookServlet.java        | /book                   | Permet d'ajouter un livre à la base de données                                               |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| HomeServlet.java        | /                       | Dirige l'utilisateur vers la page d'accueil listant les livres dans la base de données       |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| OrdersServlet.java      | /orders                 | Liste les commandes validées/enregistrées dans la DB                                         |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| PlaceOrderServlet.java  | /placeorder             | Valide la commande de l'utilisateur                                                          |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| PrepareServlet.java     | /prepare                | Ajoute des livres par défaut à la base de données                                            |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+
| RemoveBasketServlet.java| /removeBasket           | Retire des livres du panier                                                                  |
+-------------------------+-------------------------+----------------------------------------------------------------------------------------------+


##  Fonctionnalités implémentées
Les fonctionnalités implémentées sont les suivantes

### Remplissage de la base de données
Des livres exemples peuvent être insérés dans la base de données en passant par le panneau d'administration et en choisissant l'option 'Add default books'. Le même résultat peut être obtenu en passant par l'url /prepare

### Ajout de livres
En passant par /book (accessible dans le panneau d'administration /admin), un formulaire invite l'utilisateur à saisir les informations du livre. En cas de renseignement incomplet ou incorrect, un message lui signale son erreur.

### Listing des auteurs
Les auteurs ayant des livres enregistrés en base de données peuvent être listée en passant par /author.

### Ajout de livres au panier
En sélectionnant un (ou plusieurs livres) sur la page d'accueil et en cliquant sur le bouton 'Add', le(s) livre(s) sélectionné(s) est/sont ajouté(s) aux paniers.

### Visualisation du panier
À tout moment, le client peut visualiser le contenu de son panier en passant par /basket.
NB: une fois le panier validé, son contenu n'est plus disponible par cette interface.

### Validation du panier
Dans l'interface du panier (accessible en /basket), l'utilisateur peut valider sa commande en sélectionnant l'option 'Place order'.

### Visualisation des commandes passées
Depuis l'interface d'administration (/admin) il est possible de visualiser les commandes déjà validés en choisissant l'option 'list orders'. Une liste de commandes est alors disponibles affichant pour chacune d'entre elle le numéro de commande et son contenu.

## Code samples
### Gestion des erreurs à l'insertion d'un nouveau livre

```java
protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Book book = new Book();
    String author = request.getParameter("author");
    String year = request.getParameter("year");
    String title = request.getParameter("title");
    if (author == null || author.isEmpty()) {
        request.setAttribute("pageTitle", "Add a new book");
        request.setAttribute("status", "The author of the book must be given!");
        request.getRequestDispatcher("addbook.jsp").forward(request, response);
        return;
    }
    if (year == null || year.isEmpty()) {
        request.setAttribute("pageTitle", "Add a new book");
        request.setAttribute("status", "The year of the book must be given!");
        request.getRequestDispatcher("addbook.jsp").forward(request, response);
        return;
    }

    if (title == null || title.isEmpty()) {
        request.setAttribute("pageTitle", "Add a new book");
        request.setAttribute("status", "The title of the book must be given!");
        request.getRequestDispatcher("addbook.jsp").forward(request, response);
        return;
    }

    try {
        book.setAuthor(author);
        book.setYear(Integer.parseInt(year));
        book.setTitle(title);
        book = bookDao.persist(book);
    } catch (NumberFormatException e) {
        Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        request.setAttribute("pageTitle", "Add a new book");
        request.setAttribute("status", "The year of the book must be an integer!");
        request.getRequestDispatcher("addbook.jsp").forward(request, response);
        return;
    } catch (EJBException e) {
        Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        request.setAttribute("pageTitle", "Error");
        request.setAttribute("error", "An error occured while saving the new book!");
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return;
    } catch (Exception e) {
        Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        request.setAttribute("pageTitle", "Error");
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return;
    }
    response.sendRedirect("");
}
```

### Recherche des auteurs en base de données

```Java
public List<String> getAuthors() {
    TypedQuery<String> query = em.createQuery("SELECT DISTINCT(b.author) FROM Book b ORDER BY b.author", String.class);
    List<String> authors = query.getResultList();
    return authors;
}
```

### Validation de panier

```Java
protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null) {
        request.setAttribute("error", "A problem occured with your basket!");
        request.setAttribute("pageTitle", "Error");
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return;
    }
    Basket basket = (Basket) session.getAttribute("basket");
    if (basket == null) {
        session.invalidate();
        request.setAttribute("error", "The system could not find your basket!");
        request.setAttribute("pageTitle", "Error");
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return;
    }
    try {
        basket = basketDao.persist(basket);
    } catch (EJBException e) {
        Logger.getLogger(PlaceOrderServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);request.setAttribute("error", "The system could not find your basket!");
        request.setAttribute("pageTitle", "Error");
        request.setAttribute("error", "Your order could not be saved to the database!");
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return;
    }
    session.removeAttribute("basket");
    session.invalidate();
    request.setAttribute("orderNumber", basket.getId());
    request.getRequestDispatcher("orderPlaced.jsp").forward(request, response);
}
```

##  Conclusion
Ce projet  a permis d'aborder les différentes possibilités et la portabilité offerte par les environnements Java EE.

De la couche de persistance à la couche présentation en passant par le code métier, l'entièreté de l'application a pu être réalisé en Java en avec un niveau élevé de code réutilisable à chaque étape.

Ce projet a aussi révélé les difficultés de configuration qui ralentissent considérablement les premiers déploiements en environnement EE. Mais également une explosion du nombre de servlet pour la gestion des services offerts à l'internaute.

Au final, ce projet présente toutes les possibilités disponibles pour développer des applications Java tout en balançant la facilité de développement avec les aspects plus difficile de la configuration.

Merci pour votre lecture.


