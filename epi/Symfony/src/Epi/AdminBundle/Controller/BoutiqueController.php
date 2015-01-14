<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\BoutiqueBundle\Entity\Produit;
use Epi\BoutiqueBundle\Form\ProduitType;
use Epi\BoutiqueBundle\Entity\CategorieProduit;
use Epi\BoutiqueBundle\Form\CategorieProduitType;
use Epi\UserBundle\Entity\User;
use Epi\BoutiqueBundle\Entity\Panier;
use Epi\BoutiqueBundle\Entity\ProduitPanier;
use Epi\CoreBundle\Entity\News;
use Epi\CoreBundle\Form\NewsType;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Form\CommandeType;
use Epi\BoutiqueBundle\Entity\ProduitCommande;
use Epi\BoutiqueBundle\Entity\Distribution;
use Epi\BoutiqueBundle\Form\DistributionType;

class BoutiqueController extends Controller
{
	public function add_produitAction()
	{

		$produit= new Produit();
		$form = $this->createFormBuilder($produit)
			->add('nom', 'text')
            ->add('description', 'text')
            ->add('quantite', 'integer')
            ->add('prix', 'number')
			->add('categorie')
            ->add('image', 'text')
            ->getForm();
		$request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form->bind($request);
			if($form->isValid()){

				$em = $this->getDoctrine()->getManager();
				$produit->setQuantiteCommandee(0);
				$em->persist($produit);
				$em->flush();
			}

		}
		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'produits'
			)));

	}

	public function plus_produitAction($id){
		$em = $this->getDoctrine()->getManager();

		$ProduitRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$produit = $ProduitRepository->findOneById($id);
		$produit->setQuantite($produit->getQuantite()+1);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'produits'
			)));

	}

	public function moins_produitAction($id){
		$em = $this->getDoctrine()->getManager();

		$ProduitRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$produit = $ProduitRepository->findOneById($id);
		$produit->setQuantite($produit->getQuantite()-1);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'produits'
			)));
	}

	public function remove_produitAction($id){
		$em = $this->getDoctrine()->getManager();

		$ProduitRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$produit = $ProduitRepository->findOneById($id);
		$em->remove($produit);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'produits'
			)));
	}

	public function edit_produitAction($id){
		$em = $this->getDoctrine()->getManager();

		$ProduitRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$produit = $ProduitRepository->findOneById($id);
		$form_produit = $this->createFormBuilder($produit)
			->add('nom', 'text', array('label'  => 'Nom du produit :'))
            ->add('description', 'text', array('label'  => 'Description :'))
            ->add('quantite', 'integer', array('label'  => 'Quantité :'))
            ->add('prix', 'number', array('label'  => 'Prix :'))
			->add('categorie', null, array('label'  => 'Catégorie :'))
            ->add('image', 'text', array('label'  => 'Url de l image:'))
            ->getForm();

        $request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form_produit->bind($request);
			if($form_produit->isValid()){
				$em = $this->getDoctrine()->getManager();
				$em->persist($produit);
				$em->flush();
				return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'produits'
			)));
			}
		}

		return $this->render('EpiAdminBundle:Boutique:edit_produit.html.twig', array(
			'form_produit' => $form_produit->createView(),
			'produit' => $produit,
			'onglet' => 'boutique'
		));
	}





	public function add_categorieAction()
	{
		$categorie=new CategorieProduit();
		$form_categorie = $this->createForm(new CategorieProduitType(), $categorie);

		$request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form_categorie->bind($request);
			// if($form->isValid()){
				$em = $this->getDoctrine()->getManager();
				$em->persist($categorie);
				$em->flush();
			// }

		}
		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'categories'
			)));

	}

	public function remove_categorieAction($id){
		$em = $this->getDoctrine()->getManager();

		$CategorieRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:CategorieProduit');

		$categorie = $CategorieRepository->findOneById($id);
		$em->remove($categorie);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'categories'
			)) . '#categories');
	}

	public function edit_categorieAction($id){
		$em = $this->getDoctrine()->getManager();

		$CategorieRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:CategorieProduit');

		$categorie = $CategorieRepository->findOneById($id);
		$form_categorie = $this->createForm(new CategorieProduitType(), $categorie);

        $request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form_categorie->bind($request);
			if($form_categorie->isValid()){
				$em = $this->getDoctrine()->getManager();
				$em->persist($categorie);
				$em->flush();
				return $this->redirect($this->generateUrl('admin_boutique', array(
			'onglet_boutique' => 'categories'
			)));
			}
		}

		return $this->render('EpiAdminBundle:Boutique:edit_categorie.html.twig', array(
			'form_categorie' => $form_categorie->createView(),
			'categorie' => $categorie,
			'onglet' => 'boutique'
		));
	}

}


?>
