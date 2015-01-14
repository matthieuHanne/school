<?php namespace Lo\EpiBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Lo\EpiBundle\Entity\Article;


class EpiController extends Controller
{

	public function menuAction(){
		return $this->render('LoEpiBundle:Epi:menu.html.twig', array(
			'liste_articles'=> $menu
		));
	}
	public function indexAction()
	{
		return $this->render('LoEpiBundle:Epi:index.html.twig', array(
			'hashs' => array('news','connexion','inscription')
		));
	}

	public function staticAction($page){
		return $this->render('LoEpiBundle:Epi:'.$page.'.html.twig');
	}
	public function agirAction($focus){
		return $this->render('LoEpiBundle:Epi:'.$focus.'.html.twig');
	}

	

	public function boutiqueAction(){
		

	$repository = $this->getDoctrine()
	                     ->getManager()
	                     ->getRepository('LoEpiBundle:Article');

	$listeArticles=array();
	$listeArticles[0] = $repository->findAll();
	$em = $this->getDoctrine()->getManager();


	$categories=array();
	$categories[1]=array("nom" => 'Frais', "ref" => 'frais');
	$categories[2]=array("nom" => 'Epicerie salée', "ref" => 'epicerie_salee');
	$categories[3]=array("nom" => 'Epicerie sucrée', "ref" => 'epicerie_sucree');
	$categories[4]=array("nom" => 'Entretien', "ref" => 'entretien');
	$categories[5]=array("nom" => 'Hygiène', "ref" => 'hygiene');
	$categories[6]=array("nom" => 'Autres', "ref" => 'autres');

	for($i=1;$i<count($categories)+1;$i++){
		 $cat=$categories[$i];
		 $listeArticles[$cat["ref"]] = $repository->findBy(array('categorie' => $i));
	}
	 
	     
	return $this->render('LoEpiBundle:Epi:boutique.html.twig', array(
	    'listeArticles' => $listeArticles,
	    'categories' => $categories
	));
	}

	
}
