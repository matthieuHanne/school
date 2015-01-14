<?php

// CREATION DES CATEGORIES DE PRODUITS
$em = $this->getDoctrine()->getManager();
$cat0 = new CategorieProduit('Frais');
$em->persist($cat0);
$cat1 = new CategorieProduit('Epicerie salée');
$em->persist($cat1);
$cat2 = new CategorieProduit('Epicerie sucrée');
$em->persist($cat2);
$cat3 = new CategorieProduit('Hygiène');
$em->persist($cat3);
$cat4 = new CategorieProduit('Produits d\'entretien');
$em->persist($cat4);
$cat5 = new CategorieProduit('Autres');
$em->persist($cat5);
$em->flush();


// AJOUT DES PRODUITS
$categoriesRepository = $this->getDoctrine()
->getManager()
->getRepository('EpiBoutiqueBundle:CategorieProduit');
$categories=$categoriesRepository->findAll();
$em = $this->getDoctrine()->getManager();
$article1 = new Produit('Lait', "Pack de 6 litres, demi-écrémé", $categories[0], 2, 2.5, 0, 0);
$em->persist($article1);
$article2 = new Produit('Farine', "Paquet de 1kg", $categories[1], 2, 1.5, 0, 0);
$em->persist($article2);
$article3 = new Produit('Riz', "Paquet de 500g", $categories[1], 2, 0.5, 0, 0);
$em->persist($article3);
$article3 = new Produit('Chocolat', "tablette de 200g", $categories[2], 2, 1.5, 0, 0);
$em->persist($article3);
$article4 = new Produit('Liquide vaisselle', "PEC citron 500g", $categories[4], 2, 1.6, 0, 0);
$em->persist($article4);
$em->flush();


// Ajout User
$em = $this->getDoctrine()->getManager();
$userRepository = $this->getDoctrine()
	->getManager()
	->getRepository('EpiUserBundle:User');
$user=new User();
$user->setUsername("Lalou");
$user->setPassword("Lalou");
$user->setSalt("Lalou");
$em->persist($user);
$em->flush();

// Ajout Panier
$userRepository = $this->getDoctrine()
->getManager()
->getRepository('EpiUserBundle:User');
$user=$userRepository->findOneByUsername("Lalou");
$em = $this->getDoctrine()->getManager();
$p = new Panier($user);
$em->persist($p);
$em->flush();

?>