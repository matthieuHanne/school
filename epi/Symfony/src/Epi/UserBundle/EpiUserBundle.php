<?php

namespace Epi\UserBundle;

use Symfony\Component\HttpKernel\Bundle\Bundle;

class EpiUserBundle extends Bundle
{
	public function getParent()
	{
		return 'FOSUserBundle';
	}
}
