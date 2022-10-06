import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Just Semantic Version',
    Png: require('@site/static/img/version-control.png').default,
    description: (
      <>
        It's just a semantic version library
      </>
    ),
  },
  {
    title: 'Zero Dependency',
    Png: require('@site/static/img/null.png').default,
    description: (
      <>
        <code>just-semver</code> has no external dependencies.
      </>
    ),
  },
  {
    title: 'Support Scala 2 and 3',
    Png: require('@site/static/img/scala.png').default,
    description: (
      <>
        It supports both Scala 2 and Scala 3.
      </>
    ),
  },
];

function FeatureSvg({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

function FeaturePng({Png, title, description}) {
  return (
      <div className={clsx('col col--4')}>
        <div className="text--center">
          <img src={Png} className={styles.featurePng} alt={title} />
        </div>
        <div className="text--center padding-horiz--md">
          <h3>{title}</h3>
          <p>{description}</p>
        </div>
      </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
              props.hasOwnProperty('Svg') ?
                <FeatureSvg key={idx} {...props} /> :
                <FeaturePng key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
