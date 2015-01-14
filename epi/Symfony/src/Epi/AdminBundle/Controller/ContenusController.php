<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\CoreBundle\Entity\CoreData;
use Epi\CoreBundle\Form\CoreDataType;

class ContenusController extends Controller
{
	public function updateAction($id)
	{
		$em = $this->getDoctrine()->getManager();

		$CoreDataRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiCoreBundle:CoreData');

		$coreData=$CoreDataRepository->findOneById($id);
		$page=$coreData->getPage();
		$form = $this->createForm(new CoreDataType(), $coreData);
		$request = $this->get('request');
		if ($request->getMethod() == 'POST'){
			$form->bind($request);
			$coreData->setPage($page);
		}
		$em->flush();

		return $this->redirect($this->generateUrl('admin_contenus', array('onglet_contenus' => $coreData->getPage())));
	}


}


?>