export const SHOWCASES: ShowcaseConfig = {
  "0 – On-Premises": {
    baseUrl: "http://localhost:8080",
  },
  "1 – Lift & Shift": {
    baseUrl: "https://amazed-antelope.eu-central-1.cloud.workshop.openknowledge.services/",
  },
  "2 – Managed Services": {
    baseUrl: "https://q5fxkss4hb.eu-central-1.awsapprunner.com",
  },
  "3 – PaaS": {
    baseUrl: "https://nwgrq3mjwf.eu-central-1.awsapprunner.com",
  },
  "4 – Lambda": {
    baseUrl: "https://yv2kqianm7.eu-central-1.awsapprunner.com",
  },
};

type ShowcaseConfig = Record<string, SimpleShowcase | Showcase>;

interface SimpleShowcase {
  baseUrl: string;
}

interface Showcase {
  categoryBaseUrl: string;
  topicBaseUrl: string;
  postBaseUrl: string;
}

export const getShowcase = (showcaseName: string): Showcase => {
  const rawShowcase = SHOWCASES[showcaseName];

  if ("baseUrl" in rawShowcase) {
    const baseUrl = new URL(rawShowcase.baseUrl);

    const categoryBaseUrl = new URL("/categories", baseUrl).toString();
    const topicBaseUrl = new URL("/topics", baseUrl).toString();
    const postBaseUrl = new URL("/posts", baseUrl).toString();

    return {
      categoryBaseUrl,
      topicBaseUrl,
      postBaseUrl,
    };
  }

  return rawShowcase;
};

const LOCAL_STORAGE_KEY = "showcase";

export const getCurrentShowcaseName = () => {
  const showcaseName = localStorage.getItem(LOCAL_STORAGE_KEY);

  if (showcaseName) {
    return showcaseName;
  }

  return Object.keys(SHOWCASES)[0];
};

export const getCurrentShowcase = () => {
  const showcaseName = localStorage.getItem(LOCAL_STORAGE_KEY);

  if (showcaseName) {
    return getShowcase(showcaseName);
  }

  return SHOWCASES[0];
};

export const setCurrentShowcase = (showcaseName: string) => {
  localStorage.setItem(LOCAL_STORAGE_KEY, showcaseName);

  window.location.reload();
};

export const CURRENT_SHOWCASE_NAME = getCurrentShowcaseName();

export const CURRENT_SHOWCASE = getShowcase(CURRENT_SHOWCASE_NAME);
