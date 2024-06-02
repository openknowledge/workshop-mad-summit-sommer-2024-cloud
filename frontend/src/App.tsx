import { QueryClient, QueryClientProvider, useQuery } from "react-query";
import {
  Link,
  Outlet,
  RouterProvider,
  createBrowserRouter,
  useParams,
} from "react-router-dom";
import "./App.css";
import okLogo from "./assets/logo.png";
import {
  CURRENT_SHOWCASE,
  CURRENT_SHOWCASE_NAME,
  SHOWCASES,
  setCurrentShowcase,
} from "./showcases.ts";

const ShowcaseBar = () => {
  return (
    <div className="flex-none h-16 bg-ok-dark-blue">
      <div className="h-full flex flex-col items-center px-2">
        <div className="flex-grow w-full max-w-[1000px] flex items-center">
          <ShowcaseSelector />
        </div>
      </div>
    </div>
  );
};

// returns a HTML select with a few selectable showcases
const ShowcaseSelector = () => {
  const options = Object.keys(SHOWCASES);

  return (
    <div>
      <select
        value={CURRENT_SHOWCASE_NAME}
        onChange={(e) => {
          const showcaseName = e.target.value;

          setCurrentShowcase(showcaseName);
        }}
        className="p-2 outline-none border-2 bg-white hover:bg-ok-berry hover:text-white text-ok-berry border-ok-berry font-bold"
      >
        {options.map((option) => {
          return (
            <option key={option} value={option}>
              {option}
            </option>
          );
        })}
      </select>
    </div>
  );
};

const AppBar = () => {
  return (
    <div className="flex-none h-auto min-h-[4rem] bg-ok-grey-light py-1">
      <div className="h-full flex flex-col items-center px-2">
        <div className="flex-grow w-full max-w-[1000px] flex items-center justify-between">
          <div className="flex items-center">
            <div className="flex-none mr-5">
              <Link to="/">
                {" "}
                <img src={okLogo} className="h-[48px]" />
              </Link>
            </div>
            <div className="text-2xl font-bold text-ok-stone-grey">
              <Link to="/">Cloud Forum</Link>
            </div>
          </div>
          <div>
            <div>
              <input
                className="p-2 mr-2 outline-2 bg-white focus:outline-ok-magenta"
                placeholder="Email"
              />
              <input
                className="p-2 mr-2 outline-2 bg-white focus:outline-ok-magenta"
                placeholder="Password"
              />
              <button className="p-2 border-2  bg-white hover:bg-ok-magenta hover:text-white text-ok-magenta border-ok-magenta font-bold focus:outline-ok-magenta">
                Sign In
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const AppContent = () => {
  return (
    <div className="flex-grow ">
      <div className="h-full flex flex-col items-center px-2">
        <div className="flex-grow w-full max-w-[1000px]">
          <div className="py-8">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  );
};

interface Category {
  id: string;
  title: string;
  description: string;
  topicsCount: number;
  postsCount: number;
  slug: string;
}

const OverviewPage = () => {
  const { isLoading, error, data } = useQuery(
    "categories",
    () => {
      const url = new URL(CURRENT_SHOWCASE.categoryBaseUrl);

      return appFetch(url).then((res) => res.json());
    },
    { retry: false }
  );

  if (isLoading) return <div>Loading...</div>;

  if (error) return <div>{error?.toString()}</div>;

  const renderCategories = () => {
    // TODO: Validate
    const categories: Category[] = data;

    const categoriesJsx = categories.map((category) => {
      const renderSummary = () => {
        if (category.topicsCount === 0) {
          return <div className="text-ok-stone-grey">No active topics yet</div>;
        }

        return (
          <div className="text-ok-stone-grey">
            <span className="font-bold">{category.topicsCount}</span> topic(s)
            with <span className="font-bold">{category.postsCount}</span>{" "}
            post(s) in total
          </div>
        );
      };

      return (
        <div key={category.id}>
          <h2 className="font-bold text-xl mb-1 underline">
            <Link to={`/categories/${category.id}`}>{category.title}</Link>
          </h2>
          <div className="mb-1">{category.description}</div>
          <div>{renderSummary()}</div>
        </div>
      );
    });

    if (categoriesJsx.length === 0) {
      return (
        <div className="text-ok-stone-grey text-2xl">No categories yet</div>
      );
    }

    return <div className="space-y-6">{categoriesJsx}</div>;
  };

  return (
    <div>
      <h1 className="text-3xl mb-8">
        <Link to="/" className="underline">
          Categories
        </Link>
      </h1>

      {renderCategories()}
    </div>
  );
};

interface Topic {
  id: string;
  title: string;
  description: string;

  createdOn: string;
  createdBy: string;

  lastPost: string;

  postCount: number;
  userCount: number;
}

interface TopicResult {
  topics: Topic[];

  categoryId: string;
  categoryTitle: string;
}

const CategoryPage = () => {
  const { categoryId } = useParams();

  if (categoryId === undefined) {
    throw new Error("categoryId is undefined");
  }

  const { isLoading, error, data, refetch } = useQuery(
    `topics-${categoryId}`,
    () => {
      const queryParams = new URLSearchParams({
        category: categoryId,
      }).toString();

      const url = new URL(CURRENT_SHOWCASE.topicBaseUrl);

      url.search = queryParams.toString();

      return appFetch(url).then((res) => res.json());
    }
  );

  if (isLoading) return <div>Loading...</div>;

  if (error) return <div>An error has occurred: {JSON.stringify(error)}</div>;

  // TODO: Validate
  const topicResult: TopicResult = data;

  const renderTopics = () => {
    const topicsJsx = topicResult.topics.map((topic) => {
      return (
        <div key={topic.id}>
          <h2 className="font-bold text-xl mb-1 underline">
            <Link
              to={`/categories/${topicResult.categoryId}/topics/${topic.id}`}
            >
              {topic.title}
            </Link>
          </h2>
          <div className="mb-1">{topic.description}</div>
          <div className="mb-1">
            — by <span className="font-bold">{topic.createdBy}</span> on{" "}
            {new Date(topic.createdOn).toLocaleDateString()}
          </div>
          <div className="text-ok-stone-grey">
            <span className="font-bold">{topic.postCount}</span> post(s) by{" "}
            <span className="font-bold">{topic.userCount}</span> user(s) in
            total
          </div>
        </div>
      );
    });

    if (topicsJsx.length === 0) {
      return (
        <div className="text-ok-stone-grey text-2xl">No active topics yet</div>
      );
    }

    return <div className="space-y-6">{topicsJsx}</div>;
  };

  return (
    <div>
      <h1 className="text-3xl mb-8">
        <Link to="/" className="underline">
          Categories
        </Link>{" "}
        /{" "}
        <Link
          to={`/categories/${topicResult.categoryId}`}
          className="underline text-ok-stone-grey font-semibold"
        >
          {topicResult.categoryTitle}
        </Link>
      </h1>

      <div className="mb-4">{renderTopics()}</div>

      <h2 className="text-2xl mb-4">Add topic</h2>

      <div>
        <form
          action=""
          onSubmit={(e) => {
            console.log("SUBMIT");

            if (e.target instanceof HTMLFormElement) {
              const formData = new FormData(e.target);

              const title = formData.get("title");
              const description = formData.get("description");

              const queryParams = new URLSearchParams({
                category: categoryId,
              }).toString();

              const url = new URL(CURRENT_SHOWCASE.topicBaseUrl);

              url.search = queryParams.toString();

              const doStuff = async () => {
                const res = await appFetch(url, {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                  },
                  body: JSON.stringify({ title, description }),
                });

                if (res.ok) {
                  refetch();
                }
              };

              doStuff();
            }

            e.preventDefault();
          }}
        >
          <div className="mb-4">
            <div className="mb-2">
              <label>
                <span className="block mb-2">Title</span>
                <input
                  type="text"
                  name="title"
                  required
                  minLength={3}
                  className="block w-[50%] bg-ok-grey-light p-2 outline-2 focus:outline-ok-berry"
                />
              </label>
            </div>

            <div>
              <label>
                <span className="block mb-2">Description</span>
                <textarea
                  name="description"
                  required
                  minLength={3}
                  className="block w-[50%] bg-ok-grey-light p-2 outline-2 focus:outline-ok-berry"
                />
              </label>
            </div>
          </div>

          <div>
            <button className="p-2 border-2  bg-white hover:bg-ok-magenta hover:text-white text-ok-magenta border-ok-magenta font-bold focus:outline-ok-magenta">
              Add topic
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

interface Post {
  id: string;
  header: string;
  content: string;
  createdBy: string;
  createdOn: string;
}

interface PostResult {
  topicId: string;
  topicTitle: string;

  categoryId: string;
  categoryTitle: string;

  posts: Post[];
}

const TopicPage = () => {
  const { topicId, categoryId } = useParams();

  if (topicId === undefined) {
    throw new Error("topicId is undefined");
  }

  if (categoryId === undefined) {
    throw new Error("categoryId is undefined");
  }

  const { isLoading, error, data } = useQuery(
    `posts-${categoryId}-${topicId}`,
    () => {
      const queryParams = new URLSearchParams({
        category: categoryId,
        topic: topicId,
      }).toString();

      const url = new URL(CURRENT_SHOWCASE.postBaseUrl);

      url.search = queryParams.toString();

      return appFetch(url).then((res) => res.json());
    }
  );

  if (isLoading) return <div>Loading...</div>;

  if (error) return <div>An error has occurred: {JSON.stringify(error)}</div>;

  // TODO: Validate
  const postResult: PostResult = data;

  const renderPosts = () => {
    const postsJsx = postResult.posts.map((post) => {
      return (
        <div key={post.id}>
          <h2 className="font-bold text-xl mb-1/2">{post.header}</h2>
          <div className="mb-1">{post.content}</div>
          <div className="text-ok-stone-grey">
            {/* the following shows created on by author */}
            Posted by <span className="font-bold">
              {post.createdBy}
            </span> on {new Date(post.createdOn).toLocaleDateString()}
          </div>
        </div>
      );
    });

    if (postsJsx.length === 0) {
      return <div className="text-ok-stone-grey text-2xl">No posts yet</div>;
    }

    return <div className="space-y-6">{postsJsx}</div>;
  };

  return (
    <div>
      <h1 className="text-3xl mb-8">
        <Link to="/" className="underline">
          Categories
        </Link>{" "}
        /{" "}
        <Link
          to={`/categories/${postResult.categoryId}`}
          className="underline text-ok-stone-grey"
        >
          {postResult.categoryTitle}
        </Link>{" "}
        /{" "}
        <Link
          to={`/categories/${postResult.categoryId}/topics/${postResult.topicId}`}
          className="underline text-ok-stone-grey font-bold"
        >
          {postResult.topicTitle}
        </Link>
      </h1>

      <div className="mb-4">{renderPosts()}</div>
    </div>
  );
};

const queryClient = new QueryClient();

const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <>
        <QueryClientProvider client={queryClient}>
          <div className="h-full flex flex-col">
            <ShowcaseBar />

            <AppBar />

            <AppContent />
          </div>
        </QueryClientProvider>
      </>
    ),
    children: [
      {
        index: true,
        element: <OverviewPage />,
      },

      {
        path: "categories/:categoryId",
        element: <CategoryPage />,
      },

      {
        path: "categories/:categoryId/topics/:topicId",
        element: <TopicPage />,
      },
    ],
  },
]);

// a function called appFetch that behaves like fetch but throws an error on non 200 or 300 status codes
const appFetch = async (input: RequestInfo | URL, init?: RequestInit) => {
  const res = await fetch(input, init);

  if (!res.ok) {
    throw new Error(`HTTP Error – Status: ${res.status}`);
  }

  return res;
};

function App() {
  return (
    <>
      <RouterProvider router={router} />
    </>
  );
}

export default App;
