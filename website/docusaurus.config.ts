// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion
import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const algoliaConfig = require('./algolia.config.json');
const googleAnalyticsConfig = require('./google-analytics.config.json');

// const lightCodeTheme = require('prism-react-renderer/themes/github');
// const darkCodeTheme = require('prism-react-renderer/themes/dracula');
const lightCodeTheme = prismThemes.nightOwlLight;
const darkCodeTheme = prismThemes.nightOwl;

const isEmptyObject = (obj: object) => Object.keys(obj).length === 0;

const isSearchable = !isEmptyObject(algoliaConfig);
const hasGoogleAnalytics = !isEmptyObject(googleAnalyticsConfig);

const classicConfig = {
  docs: {
    path: '../generated-docs/docs/',
    sidebarPath: require.resolve('./sidebars.js'),
    lastVersion: 'current',
    "versions": {
      "current": {
        "label": "0.13.0"
      },
    }
    // Please change this to your repo.
    // Remove this to remove the "edit this page" links.
  },
  theme: {
    customCss: require.resolve('./src/css/custom.css'),
  },
}


if (hasGoogleAnalytics) {
  classicConfig['gtag'] = googleAnalyticsConfig;
}

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Just SemVer',
  tagline: 'Just a Semantic Version Library',
  url: 'https://just-semver.kevinly.dev',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.png',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'Kevin-Lee', // Usually your GitHub org/user name.
  projectName: 'just-semver', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      (classicConfig),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      image: 'img/poster.png',
      navbar: {
        title: 'Just SemVer',
        logo: {
          alt: 'Just SemVer',
          src: 'img/just-semver-logo-32x32.png',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Docs',
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownActiveClassDisabled: true,
            dropdownItemsAfter: [
              {
                to: '/versions',
                label: 'All versions',
              },
            ],
          },
          {
            href: 'https://github.com/Kevin-Lee/just-semver',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Getting Started',
                to: '/docs',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/Kevin-Lee/just-semver',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Just SemVer written by <a href="https://github.com/Kevin-Lee" target="_blank"><b>Kevin Lee</b></a>, The website built with Docusaurus.
        <div>
        <a href="https://www.flaticon.com/free-icons/history" title="history icons">History icons created by juicy_fish - Flaticon</a>,
        <a href="https://www.flaticon.com/free-icons/null" title="null icons">Null icons created by Freepik - Flaticon</a> and 
        <a href="https://www.flaticon.com/free-icons/scala" title="scala icons">Scala icons created by Freepik - Flaticon</a>
        </div>
        `,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: [
          'java',
          'scala',
        ],
      }
    }),
};

if (isSearchable) {
  config['themeConfig']['algolia'] = algoliaConfig;
}

module.exports = config;
