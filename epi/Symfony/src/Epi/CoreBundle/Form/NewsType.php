<?php

namespace Epi\CoreBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class NewsType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('date',		'date')
            ->add('title',		'text')
            ->add('author',		'text')
            ->add('contain',	'textarea')
            ->add('publication','checkbox', array('required' => false))
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Epi\CoreBundle\Entity\News'
        ));
    }

    public function getName()
    {
        return 'epi_corebundle_newstype';
    }
}
