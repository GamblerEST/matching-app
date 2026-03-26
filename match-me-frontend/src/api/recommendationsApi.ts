export async function GetRecommendations() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/recommendations", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
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

export async function DismissRecommendation(userId: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/recommendations/" + userId + "/dismiss", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  return response.status;
}
