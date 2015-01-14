<?php namespace Epi\CoreBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\CoreBundle\Entity\News;
use Epi\CoreBundle\Entity\CoreData;
use Symfony\Component\HttpFoundation\Request;
use JMS\SecurityExtraBundle\Annotation\Secure;


class CoreController extends Controller
{

	public function staticAction($page){
		if($page=='index'){
			$repository = $this->getDoctrine()
				->getManager()
				->getRepository('EpiCoreBundle:News');

			$listeNews = $repository->findNews();

			return $this->render('EpiCoreBundle:Core:index.html.twig', array('listeNews' => $listeNews ));
		}
		else{
			$CoreDataRepository = $this->getDoctrine()
			->getManager()
			->getRepository('EpiCoreBundle:CoreData');

			$coreDatas=array();
			$response=$CoreDataRepository->findAll();
			foreach ($response as $value) {
				$coreDatas[$value->getPage()]=$value->getValue();
			}

			return $this->render('EpiCoreBundle:Core:'.$page.'.html.twig', array(
				'coreDatas' => $coreDatas
			));
		}
			
	}

}
