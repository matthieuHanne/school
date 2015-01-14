<?php

namespace Epi\AdminBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Epi\UserBundle\Entity\User;
use Epi\CoreBundle\Entity\News;
use Epi\CoreBundle\Form\NewsType;

class NewsController extends Controller
{

	public function putnewsAction($id)
	{

		$news = $this->getDoctrine()
			->getRepository('Epi\CoreBundle\Entity\News')
			->find($id);
		$form= $this->createForm(new NewsType, $news);

		$old_date = $news->getDate();
		$request = $this->get('request');
		if ($request->getMethod() == 'POST') {
			$form->bind($request);

			if ($form->isValid()) {
				$em = $this->getDoctrine()->getManager();
				$news->setDate($old_date);
				$em->persist($news);
				$em->flush();

				return $this->redirect($this->generateUrl('admin_news'));
			}
		}

		return $this->render('EpiAdminBundle:News:putnews.html.twig', array(
			'form' => $form->createView(),
			'news' => $news,
			'onglet' => 'news'
		));
	}

	public function delnewsAction($id)
	{
		$news = $this->getDoctrine()
			->getRepository('Epi\CoreBundle\Entity\News')
			->find($id);
		$em = $this->getDoctrine()->getManager();
		$em->remove($news);
		$em->flush();

		return $this->redirect($this->generateUrl('admin_news'));
	}
}

