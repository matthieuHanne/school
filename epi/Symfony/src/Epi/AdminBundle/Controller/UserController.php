<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Epi\BoutiqueBundle\Entity\Panier;
use Epi\BoutiqueBundle\Entity\ProduitPanier;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Entity\ProduitCommande;

class UserController extends Controller
{

	public function deluserAction($username)
	{
		$userManager = $this->get('fos_user.user_manager');
		$user = $userManager->findUserByUsername($username);

		$em = $this->getDoctrine()->getManager();
		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');
		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');
		$panierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Panier');
		$ProduitPanierRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitPanier');

		$commandes=$CommandesRepository->findByUser($user);
		foreach ($commandes as $commande) {
			$produitCommandes=$ProduitCommandeRepository->findByCommande($commande);
			foreach ($produitCommandes as $produitCommande) {
				$produitCommande->getProduit()->setQuantiteCommandee($produitCommande->getProduit()->getQuantiteCommandee()-$produitCommande->getQuantite());
				$em->remove($produitCommande);
			}
			$em->remove($commande);
		}

		$paniers=$panierRepository->findByUser($user);
		foreach ($paniers as $panier) {
			$produitPaniers=$ProduitPanierRepository->findByPanier($panier);
			foreach ($produitPaniers as $produitPanier) {
				$em->remove($produitPanier);
			}
			$em->remove($panier);
		}
		$em->flush();

		$userManager->deleteUser($user);
		return $this->redirect($this->generateUrl('admin_users'));
	}
	
	public function modifuserAction($username, Request $request)
	{
		$username = urldecode($username); 
		$userManager = $this->get('fos_user.user_manager');
		$formFactory = $this->container->get('fos_user.registration.form.factory');
		$user = $userManager->findUserByUsername($username);
		$old_user = $user;
		$form = $formFactory->createForm();
        $form->setData($user);
		$mail = $user->getEmail();

		if ('POST' === $request->getMethod()) {
            $form->bind($request);
			$data = $form->getData();
			if (strcmp($data->getEmail(), $mail)){
				//changer l'email
				$user = $old_user;
				$user->setEmail($data->getEmail());
                $userManager->updateUser($user);
				return new RedirectResponse($this->container->get('router')->generate('admin_users'));
			}

            if ($form->isValid()) {
				// changer le mot de passe
                $userManager->updateUser($user);
                return new RedirectResponse($this->container->get('router')->generate('admin_users'));
            }
        }
		
		return $this->render('EpiAdminBundle:Default:modifusers.html.twig', array(
			'user' => $user,
			'onglet' => 'users',
			'form' => $form->createView()
		));
	}
	
	public function promoteAction($username)
	{
		$usernamedec = urldecode($username); 
		$userManager = $this->get('fos_user.user_manager');
		$user = $userManager->findUserByUsername($usernamedec);
		$user->addRole('ROLE_ADMIN');
		$userManager->updateUser($user);
		
		return new RedirectResponse($this->container->get('router')->generate('admin_modifuser', array('username'=> $username)));
	}
	
	public function demoteAction($username)
	{
		$usernamedec = urldecode($username); 
		$userManager = $this->get('fos_user.user_manager');
		$user = $userManager->findUserByUsername($usernamedec);
		$user->removeRole('ROLE_ADMIN');
		$userManager->updateUser($user);
		
		return new RedirectResponse($this->container->get('router')->generate('admin_modifuser', array('username'=> $username)));
	}
}

