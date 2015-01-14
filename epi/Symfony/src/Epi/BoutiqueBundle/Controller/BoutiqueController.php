<?php

namespace Epi\BoutiqueBundle\Controller;

use Epi\BoutiqueBundle\Entity\Produit;
use Epi\BoutiqueBundle\Entity\CategorieProduit;
use Epi\UserBundle\Entity\User;
use Epi\BoutiqueBundle\Entity\Panier;
use Epi\BoutiqueBundle\Entity\ProduitPanier;
use Epi\BoutiqueBundle\Entity\ProduitCommande;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Entity\Distribution;
use Epi\BoutiqueBundle\Entity\CommandeType;

use Symfony\Component\HttpFoundation\Request;
use JMS\SecurityExtraBundle\Annotation\Secure;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class BoutiqueController extends Controller
{
	/**
	 * @Secure(roles="ROLE_USER")
	 */
	public function indexAction(){

		$userManager = $this->get('fos_user.user_manager');

		// $user = $userManager->findUserBy(array('username' => 'hibou'));
		$user = $this->container->get('security.context')->getToken()->getUser();


		$produitsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$categoriesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:CategorieProduit');

		$panierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Panier');

		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$userRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiUserBundle:User');

		$em = $this->getDoctrine()->getManager();

		$listeArticles=array();
		$listeArticles[0] = $produitsRepository->findAll();

		$produits_quantite=array();
		foreach ($listeArticles[0] as $produit) {
			$produits_quantite[$produit->getId()]=$produit->getQuantite()-$produit->getQuantiteCommandee();
		}

		$categories=$categoriesRepository->findAll();


		$em->flush();
		$categoriesKey=array();
		foreach ($categories as $categorie) {
			$categoriesKey[$categorie->getId()]=$categorie->getNom();
			$listeArticles[$categorie->getId()] = $produitsRepository->findBy(array('categorie' => $categorie->getId()));
		}

		// Formulaire de recherche
		$defaultData = array('recherche' => '');
		$form = $this->createFormBuilder($defaultData)
			->add('recherche', 'text')
			->getForm();


		// Recherche en cours
		$request=$this->getRequest();
		if ($request->isMethod('POST')) {
			$form->bind($request);
			$data = $form->getData();
			// $results=$produitsRepository->findBy(array('nom' => $data));

			// $qb = $em->createQueryBuilder('Produit');
			// $qb->where(
			//          $qb->expr()->like('p.nom', ':nom')
			//      )
			//      ->setParameter('nom','%'.$data['recherche'].'%')
			//      ->getQuery()
			//      ->getResult();

			$results=$em->createQuery('select p from EpiBoutiqueBundle:Produit p WHERE p.nom LIKE :recherche or p.description LIKE :recherche')
                ->setParameter('recherche','%'.$data['recherche'].'%')
                ->getResult();

			if($results==null) $results="Pas de resultat";
		}
		else $results=null;

		// $user=$userRepository->findOneByUsername("user");
		$panier = $panierRepository->findOneByUser($user);
		if(!$panier){
			$panier=new Panier($user);
			$em->persist($panier);
			$em->flush();
		}

		$panierProduit = $ProduitPanierRepository->findByPanier($panier);

		$bool=false;
		foreach ($panierProduit as $value) {
			$pQtte=$value->getProduit()->getQuantite();
			$pQtteCommandee=$value->getProduit()->getQuantiteCommandee();
			$ppQtte=$value->getQuantite();
			if($pQtte<=0 or $pQtte<$ppQtte or $pQtte<$pQtteCommandee){
				$em->remove($value);
				$bool=true;
			}
		}
		if($bool){
			$em->flush();
			$panierProduit = $ProduitPanierRepository->findByPanier($panier);
		}
		

		$total=0;
		foreach ($panierProduit as $pp) {
			$produits_quantite[$pp->getProduit()->getId()]-=$pp->getQuantite();
			$total+=$pp->getProduit()->getPrix()*$pp->getQuantite();
		}



		return $this->render('EpiBoutiqueBundle:Default:boutique.html.twig', array(
			'listeArticles' => $listeArticles,
			'categoriesKey' => $categoriesKey,
			'categories' => $categories,
			'form' => $form->createView(),
			'resultSearch' => $results,
			'panier' => $panierProduit,
			'total' => $total,
			'produits_quantite' => $produits_quantite
		));
	}

	public function addAction($id, $from){


		$em = $this->getDoctrine()->getManager();
		$produitsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');
		$categoriesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:CategorieProduit');
		$panierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Panier');
		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');
		$userRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiUserBundle:User');


		$userManager = $this->get('fos_user.user_manager');
		// $user = $userManager->findUserBy(array('username' => 'hibou'));
		$user = $this->container->get('security.context')->getToken()->getUser();


		$panier = $panierRepository->findOneByUser($user);
		$panierProduit = $ProduitPanierRepository->findByPanier($panier);
		$produit = $produitsRepository->findOneById($id);

		$bool=false;
		foreach ($panierProduit as $pp) {
			if($pp->getProduit()->getId()==$produit->getId()){
				$pp->setQuantite($pp->getQuantite()+1);
				$bool=true;
				break;
			}
		}
		if(!$bool){
			$pp= new ProduitPanier($produit, $panier, 1);
			$em->persist($pp);
		}
		$em->flush();

		return $this->redirect($this->generateUrl('boutique_'.$from));

	}

	public function plusAction($id, $from){
		$em = $this->getDoctrine()->getManager();

		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$panierProduit = $ProduitPanierRepository->findOneById($id);
		$panierProduit->setQuantite($panierProduit->getQuantite()+1);
		$em->flush();

		return $this->redirect($this->generateUrl('boutique_'.$from));

	}

	public function moinsAction($id, $from){
		$em = $this->getDoctrine()->getManager();

		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$panierProduit = $ProduitPanierRepository->findOneById($id);
		if($panierProduit->getQuantite()==1) $em->remove($panierProduit);
		else $panierProduit->setQuantite($panierProduit->getQuantite()-1);
		$em->flush();

		return $this->redirect($this->generateUrl('boutique_'.$from));

	}

	public function removeAction($id, $from){
		$em = $this->getDoctrine()->getManager();

		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$panierProduit = $ProduitPanierRepository->findOneById($id);
		$em->remove($panierProduit);
		$em->flush();

		return $this->redirect($this->generateUrl('boutique_'.$from));
	}


	public function validerAction(){

		$userManager = $this->get('fos_user.user_manager');
		$user = $this->container->get('security.context')->getToken()->getUser();


		$panierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Panier');

		$ProduitRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$DistributionsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');

		
		$em = $this->getDoctrine()->getManager();

		$panier = $panierRepository->findOneByUser($user);
		$panierProduit = $ProduitPanierRepository->findByPanier($panier);

		$produits=$ProduitRepository->findAll();





		$commande=new Commande();
		$formBuilder = $this->createFormBuilder($commande);
		$formBuilder->add('distribution', 'entity', array(
		  	  'class'    => 'EpiBoutiqueBundle:Distribution',
			  'multiple' => false)
		);
		$form = $formBuilder->getForm();


		$request = $this->get('request');
		if ($request->getMethod() == 'POST') {
			$form->bind($request);


			$commande->setUser($user);
			$commande->setDate(date_create());

			foreach ($panierProduit as $value) {
				$produitCommande=new produitCommande();
				$produitCommande->setProduit($value->getProduit());
				$produitCommande->setCommande($commande);
				$produitCommande->setQuantite($value->getQuantite());
				$value->getProduit()->setQuantiteCommandee($value->getQuantite()+$value->getProduit()->getQuantiteCommandee());
				$em->persist($produitCommande);
				$em->remove($value);
			}

			$em->remove($panier);
			$em->persist($commande);

			$em->flush();


			return $this->render('EpiBoutiqueBundle:Default:validation_ok.html.twig', array(
				'commande' => $commande
			));	
	    }
		


		$produits_quantite=array();
		foreach ($produits as $produit) {
			$produits_quantite[$produit->getId()]=0;
		}

		$total=0;
		foreach ($panierProduit as $pp) {
			$total+=$pp->getProduit()->getPrix()*$pp->getQuantite();
			$produits_quantite[$pp->getProduit()->getId()]=$pp->getQuantite();
		}

		$distributions = $DistributionsRepository->findAll();




		return $this->render('EpiBoutiqueBundle:Default:validation.html.twig', array(
			'panier' => $panierProduit,
			'total' => $total,
			'produits_quantite' => $produits_quantite,
			'distributions' => $distributions,
			'form' => $form->createView()
		));
	}

	public function valider_okAction($commande){
		
	}


	public function databaseAction(){
		$Repository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');
		
		// $user=$userRepository->findOneByUsername("user");
		$em = $this->getDoctrine()->getManager();
		$p = new Distribution();
		$p->setDate(date_create());
		$em->persist($p);
		$em->flush();

		return "ok";
	}
}
