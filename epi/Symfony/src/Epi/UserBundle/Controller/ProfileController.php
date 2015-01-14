<?php

/*
 * This file is part of the FOSUserBundle package.
 *
 * (c) FriendsOfSymfony <http://friendsofsymfony.github.com/>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace Epi\UserBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use FOS\UserBundle\FOSUserEvents;
use FOS\UserBundle\Event\FormEvent;
use FOS\UserBundle\Event\FilterUserResponseEvent;
use FOS\UserBundle\Event\GetResponseUserEvent;
use FOS\UserBundle\Model\UserInterface;
use Symfony\Component\DependencyInjection\ContainerAware;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;
use Epi\UserBundle\Entity\User;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Entity\ProduitCommande;

/**
 * Controller managing the user profile
 *
 * @author Christophe Coevoet <stof@notk.org>
 */
 

class ProfileController extends ContainerAware
{

	public function getDoctrine()
	{
		if (!$this->container->has('doctrine')) {
				throw new \LogicException('The DoctrineBundle is not registered in your application.');
	}

    return $this->container->get('doctrine');
	}
	
	
    /**
     * Show the user
     */	
    public function showAction()
    {
        $user = $this->container->get('security.context')->getToken()->getUser();
        if (!is_object($user) || !$user instanceof UserInterface) {
            throw new AccessDeniedException('This user does not have access to this section.');
        }

		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');
		
		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');
		
		/* On recupere l'ID de l'utilisateur courant pour creer les requetes */
		$iduser = $this->container->get('fos_user.user_manager');
		$iduser = $iduser->findUserByUsername($user);
		$iduser = $iduser->getID();
		
		/* On cree la requete pour les commandes */
		$queryBuilder = $CommandesRepository->createQueryBuilder('C');
		$queryBuilder->where("C.user = $iduser");
		$query = $queryBuilder->getQuery();
		$commandes = $query->getResult();

		$commandes_array=array();
		if($commandes) {
			$i = 0;
			$listeCommandes = array();
			foreach ($commandes as $commande) {
				$commandes_array[$commande->getId()]=array(
					'total' => 0,
					);
				$listeCommandes[$i]=$commande->getId();
				$i = $i + 1;
			}
			$listeCommandes = "(".implode(",",$listeCommandes).")";

			/* On cree la requete pour les produits des commandes */
			$queryBuilder = $ProduitCommandeRepository->createQueryBuilder('PC');
			$queryBuilder->where("PC.commande IN $listeCommandes");
			$query = $queryBuilder->getQuery();
			$produitCommandes = $query->getResult();

			foreach ($produitCommandes as $produitCommande) {
				$id=$produitCommande->getCommande()->getId();
				$prix=$produitCommande->getProduit()->getPrix()*$produitCommande->getQuantite();
				$commandes_array[$id]['total']+=$prix;
				$commandes_array[$id]['produitCommande'][]=$produitCommande;
			}
		}
		
        return $this->container->get('templating')->renderResponse('FOSUserBundle:Profile:show.html.'.$this->container->getParameter('fos_user.template.engine'), array('user' => $user, 'commandes' => $commandes, 'commandes_array' => $commandes_array));
    }

    /**
     * Edit the user
     */
    public function editAction(Request $request)
    {
        $user = $this->container->get('security.context')->getToken()->getUser();
        if (!is_object($user) || !$user instanceof UserInterface) {
            throw new AccessDeniedException('This user does not have access to this section.');
        }

        /** @var $dispatcher \Symfony\Component\EventDispatcher\EventDispatcherInterface */
        $dispatcher = $this->container->get('event_dispatcher');

        $event = new GetResponseUserEvent($user, $request);
        $dispatcher->dispatch(FOSUserEvents::PROFILE_EDIT_INITIALIZE, $event);

        if (null !== $event->getResponse()) {
            return $event->getResponse();
        }

        /** @var $formFactory \FOS\UserBundle\Form\Factory\FactoryInterface */
        $formFactory = $this->container->get('fos_user.profile.form.factory');

        $form = $formFactory->createForm();
        $form->setData($user);

        if ('POST' === $request->getMethod()) {
            $form->bind($request);

            if ($form->isValid()) {
                /** @var $userManager \FOS\UserBundle\Model\UserManagerInterface */
                $userManager = $this->container->get('fos_user.user_manager');

                $event = new FormEvent($form, $request);
                $dispatcher->dispatch(FOSUserEvents::PROFILE_EDIT_SUCCESS, $event);

                $userManager->updateUser($user);

                if (null === $response = $event->getResponse()) {
                    $url = $this->container->get('router')->generate('fos_user_profile_show');
                    $response = new RedirectResponse($url);
                }

                $dispatcher->dispatch(FOSUserEvents::PROFILE_EDIT_COMPLETED, new FilterUserResponseEvent($user, $request, $response));

                return $response;
            }
        }

        return $this->container->get('templating')->renderResponse(
            'FOSUserBundle:Profile:edit.html.'.$this->container->getParameter('fos_user.template.engine'),
            array('form' => $form->createView())
        );
    }
	
	public function remove_commandeAction($id){
		
		$user = $this->container->get('security.context')->getToken()->getUser();
		$em = $this->getDoctrine()->getManager();

		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');
		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');

		$commande=$CommandesRepository->findOneById($id);
		$produitCommandes=$ProduitCommandeRepository->findByCommande($commande);
		if($user == $commande->getUser()) {
			foreach ($produitCommandes as $value) {
				$value->getProduit()->setQuantiteCommandee($value->getProduit()->getQuantiteCommandee()-$value->getQuantite());
				$em->remove($value);
			}
			$em->remove($commande);
			$em->flush();
			
		} else {
			throw new AccessDeniedException('Vous n\'avez pas les droits nécessaires pour effectuer cette action.');
		}
		
		return new RedirectResponse($this->container->get('router')->generate('fos_user_profile_show')); 
	}
}
