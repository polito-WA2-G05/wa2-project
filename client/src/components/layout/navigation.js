const productsPaths = {
    title: "Products",
    paths: [
        {url: "/products", label: "All products"},
        {url: "/products/search", label: "Search product"},
    ]
}

const ticketsPaths = {
    title: "Tickets",
    paths: [
        {url: "/tickets", label: "Tickets"},
        {url: "/tickets/search", label: "Search ticket"},
    ]
}

const profilePaths = {
    title: "Profile",
    paths: [
        {url: "/me", label: "Profile"},
    ]
}

const customerProfilePaths = {
    ...profilePaths,
    paths: profilePaths.paths.concat([
        {url: "/me/purchases", label: "My purchased products"},
        {url: "/me/purchases/register", label: "Register purchase"},
    ])
}

const managerPaths = {
    title: "Manager",
    paths: [
        {url: "/manager/experts", label: "Experts"},
        {url: "/manager/experts/new", label: "Create expert"},
        {url: "/manager/changes", label: "Changes Log"},
    ]
}

export const navigation = {
    customer: [productsPaths, ticketsPaths, customerProfilePaths],
    expert: [productsPaths, ticketsPaths, profilePaths],
    manager: [productsPaths, ticketsPaths, managerPaths]
}

export const guestNavigation = [
    {url: "/products/search", label: "Search product"},
    {url: "/products", label: "All Products"},
    {url: "/login", label: "Login"},
]