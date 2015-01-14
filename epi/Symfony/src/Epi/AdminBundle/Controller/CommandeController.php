<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Entity\ProduitCommande;

class CommandeController extends Controller
{
	public function remove_commandeAction($id){

		$em = $this->getDoctrine()->getManager();

		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');
		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');

		$commande=$CommandesRepository->findOneById($id);
		$produitCommandes=$ProduitCommandeRepository->findByCommande($commande);
		foreach ($produitCommandes as $value) {
			$value->getProduit()->setQuantiteCommandee($value->getProduit()->getQuantiteCommandee()-$value->getQuantite());
			$em->remove($value);
		}
		$em->remove($commande);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_commandes'));

	}

	public function valider_commandeAction($id)
	{
		$em = $this->getDoctrine()->getManager();

		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');

		$ProduitCommandeRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:ProduitCommande');

		$commande=$CommandesRepository->findOneById($id);
		$produitCommandes=$ProduitCommandeRepository->findByCommande($commande);

		foreach ($produitCommandes as $produitCommande) {
			$p=$produitCommande->getProduit();
			$p->setQuantite($p->getQuantite() - $produitCommande->getQuantite());
			$p->setQuantiteCommandee($p->getQuantiteCommandee() - $produitCommande->getQuantite());
			$em->remove($produitCommande);
		}
		$em->remove($commande);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_commandes'));
	}

}


?>