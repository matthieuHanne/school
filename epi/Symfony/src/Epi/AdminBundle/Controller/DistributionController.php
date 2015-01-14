<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\BoutiqueBundle\Entity\Commande;
use Epi\BoutiqueBundle\Entity\Distribution;
use Epi\BoutiqueBundle\Form\DistributionType;

class DistributionController extends Controller
{
	public function distribution_removeAction($id)
	{
		$em = $this->getDoctrine()->getManager();

		$DistributionsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');
		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');

		$distribution=$DistributionsRepository->findOneById($id);
		$commandes=$CommandesRepository->findByDistribution($distribution);
		if(count($commandes)>0){
			return $this->redirect('admin_distributions');
		}


		$em->remove($distribution);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_distributions'));
	}

	public function distribution_addAction()
	{
		$DistributionsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');
		$CommandesRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Commande');

		$distribution=new Distribution();
		$form = $this->createForm(new DistributionType(), $distribution);
 
	    $request = $this->get('request');
	    if ($request->getMethod() == 'POST') {
	      $form->bind($request);
	      $em = $this->getDoctrine()->getManager();
	      $em->persist($distribution);
	      $em->flush();
	 	 }
	  	return $this->redirect($this->generateUrl('admin_distributions'));
	}

	public function edit_distributionAction($id){
		$em = $this->getDoctrine()->getManager();

		$DistributionsRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiBoutiqueBundle:Distribution');

		$distribution = $DistributionsRepository->findOneById($id);
		$form = $this->createForm(new DistributionType(), $distribution);

        $request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form->bind($request);
			if($form->isValid()){
				$em = $this->getDoctrine()->getManager();
				$em->persist($distribution);
				$em->flush();
				return $this->redirect($this->generateUrl('admin_distributions'));
			}
		}

		return $this->render('EpiAdminBundle:Distribution:edit_distribution.html.twig', array(
			'form' => $form->createView(),
			'distribution' => $distribution,
			'onglet' => 'distributions'
		));
		
	}


}


?>