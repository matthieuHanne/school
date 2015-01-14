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
use Epi\BoutiqueBundle\Entity\ProduitCommande;
use Epi\BoutiqueBundle\Entity\Distribution;
use Epi\BoutiqueBundle\Form\DistributionType;
use Epi\CoreBundle\Form\CoreDataType;
use FOS\UserBundle\FOSUserEvents;
use FOS\UserBundle\Model\UserInterface;
use FOS\UserBundle\Event\FormEvent;
use FOS\UserBundle\Event\GetResponseUserEvent;
use FOS\UserBundle\Event\UserEvent;
use FOS\UserBundle\Event\FilterUserResponseEvent;
use Symfony\Component\DependencyInjection\ContainerAware;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;

class AdminController extends Controller
{

	public function boutiqueAction($onglet_boutique="produits")
	{
		//formulaire
		$produit= new Produit();
		$form_produit = $this->createFormBuilder($produit)
			->add('nom', 'text', array('label'  => 'Nom du produit :'))
            ->add('description', 'text', array('label'  => 'Description :'))
            ->add('quantite', 'integer', array('label'  => 'Quantité :'))
            ->add('prix', 'number', array('label'  => 'Prix :'))
			->add('categorie', null, array('label'  => 'Catégorie :'))
            ->add('image', 'text', array('label'  => 'Url de l image:'))
            ->getForm();
			
        $form_categorie = $this->createForm(new CategorieProduitType(), new CategorieProduit());

		$produitsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Produit');

		$categoriesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:CategorieProduit');

		$produits=$produitsRepository->findAll();
		$categories=$categoriesRepository->findAll();

		$nbre_produits=array();

		foreach ($categories as $categorie) {
			$nbre_produits[$categorie->getId()]=0;
		}

		foreach ($produits as $produit) {
			$nbre_produits[$produit->getCategorie()->getId()]++;
		}
		return $this->render('EpiAdminBundle:Default:boutique.html.twig', array(
			'produits' => $produits,
			'categories' => $categories,
			'nbre_produits' => $nbre_produits,
			'onglet' => 'boutique',
			'form_produit' => $form_produit->createView(),
			'form_categorie' => $form_categorie->createView(),
			'onglet_boutique' => $onglet_boutique
		));
	}

	
	public function usersAction(Request $request)
	{

		$userManager = $this->get('fos_user.user_manager');
		$users = $userManager->findUsers();
		
		$formFactory = $this->container->get('fos_user.registration.form.factory');
        $dispatcher = $this->container->get('event_dispatcher');

        $user = $userManager->createUser();
        $user->setEnabled(true);

        /*$dispatcher->dispatch(FOSUserEvents::REGISTRATION_INITIALIZE, new UserEvent($user, $request));*/

        $form = $formFactory->createForm();
        $form->setData($user);

        if ('POST' === $request->getMethod()) {
            $form->bind($request);

            if ($form->isValid()) {
                $event = new FormEvent($form, $request);
                /*$dispatcher->dispatch(FOSUserEvents::REGISTRATION_SUCCESS, $event);*/

                $userManager->updateUser($user);

                if (null === $response = $event->getResponse()) {
                    $url = $this->container->get('router')->generate('admin_users');
                    $response = new RedirectResponse($url);
                }

                /*$dispatcher->dispatch(FOSUserEvents::REGISTRATION_COMPLETED, new FilterUserResponseEvent($user, $request, $response));*/

                return $response;
            }
			else {
				return $this->render('EpiAdminBundle:Default:users.html.twig', array(
				'users' => $users,
				'onglet' => 'users',
				'form' => $form->createView(),
				'reg_failure' => true
			));
			}
        }

		return $this->render('EpiAdminBundle:Default:users.html.twig', array(
			'users' => $users,
			'onglet' => 'users',
			'form' => $form->createView()
		));
	}

	public function commandesAction()
	{
		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');

		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');

		$commandes=$CommandesRepository->findAll();
		$commandes_array=array();
		foreach ($commandes as $commande) {
			$commandes_array[$commande->getId()]=array(
				'total' => 0,
				);
		}



		$produitCommandes=$ProduitCommandeRepository->findAll();
		foreach ($produitCommandes as $produitCommande) {
			$id=$produitCommande->getCommande()->getId();
			$prix=$produitCommande->getProduit()->getPrix()*$produitCommande->getQuantite();
			$commandes_array[$id]['total']+=$prix;
			$commandes_array[$id]['produitCommande'][]=$produitCommande;
		}

		return $this->render('EpiAdminBundle:Default:commandes.html.twig', array(
			'commandes' => $commandes,
			'commandes_array' => $commandes_array,
			'onglet' => 'commandes'
		));
	}


	



	public function contenusAction($onglet_contenus=null)
	{

		// Edition des contenus
		$coreDataRepository=$this->getDoctrine()
			->getManager()
			->getRepository('EpiCoreBundle:CoreData');
		$coreDatas = $coreDataRepository->findAll();

		$coreDatas_forms=array();
		foreach ($coreDatas as $coreData) {
			$form = $this->createForm(new CoreDataType(), $coreData);
			$coreDatas_forms[$coreData->getPage()]=$form->createView();
		}

		$form = $this->createForm(new CoreDataType(), $coreDatas[0]);

		return $this->render('EpiAdminBundle:Default:contenus.html.twig', array(
			'coreDatas_forms' => $coreDatas_forms,
			'onglet' => 'contenus',
			'coreDatas' => $coreDatas,
			'form' => $form->createView(),
			'onglet_contenus' => $onglet_contenus
		));
	}

	public function newsAction()
	{
		//formulaire
		$news = new News();
		$form= $this->createForm(new NewsType, $news);
		$request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form->bind($request);
			if($form->isValid()){
				$news->setDate(new \DateTime("now"));
				$em = $this->getDoctrine()->getManager();
				$em->persist($news);
				$em->flush();
			}
			return $this->redirect('news');
		}

		// News list
		$newsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiCoreBundle:News');

		$news=$newsRepository->createQueryBuilder('N')->orderBy('N.date', 'DESC')->getQuery()->getResult();

		return $this->render('EpiAdminBundle:Default:news.html.twig', array(
			'news' => $news,
			'form' => $form->createView(),
			'onglet' => 'news',
		));
	}

	

	public function distributionsAction()
	{
		$em = $this->getDoctrine()->getManager();

		$DistributionsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');
		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');

		$distributions = $DistributionsRepository->findAll();
		$distributions_commandes=array();

		foreach ($distributions as $distribution) {
			$commandes=$CommandesRepository->findByDistribution($distribution);
			$distributions_commandes[$distribution->getId()]=count($commandes);
		}

		$form = $this->createForm(new DistributionType(), new Distribution());


		return $this->render('EpiAdminBundle:Default:distributions.html.twig', array(
			'onglet' => 'distributions',
			'distributions' => $distributions,
			'distributions_commandes' => $distributions_commandes,
			'form' => $form->createView()
		));
	}

	
}
