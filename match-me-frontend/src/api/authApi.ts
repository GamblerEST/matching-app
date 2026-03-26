export async function AuthLogin(data: { email: string; password: string }) {
  const response = await fetch("/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  const responseData = await response.json();
  return responseData;
}

export async function AuthRegister(data: {
  email: string;
  username: string;
  password: string;
}) {
  const response = await fetch("/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  return await response.json();
}
