// Imports
import { Routes, Route, useLocation } from "react-router-dom";

// Views
import {
	Home,
	Error,
	Products,
	Product,
	Profile,
	Login,
	Signup,
	CreateExpert,
	EditProfile,
	SearchTicket,
	Tickets,
	Ticket,
	CreateTicket,
	Changes,
	Experts,
	Chat
} from "@views";

// Components
import { Layout } from "@components/layout";
import { ProtectedRoute, GuestRoute, RoleRoute } from "@components/security";

function App() {
	const location = useLocation();

	return (
		<Routes location={location} key={location.pathname}>
			<Route element={<Layout />}>
				<Route index path="/" element={<Home />} />
					<Route path="chat" element={<Chat/>} />
				<Route element={<GuestRoute />}>
					<Route path="/login" element={<Login />} />
					<Route path="/signup" element={<Signup />} />
				</Route>
				<Route path="/products" element={<Products />} />
				<Route path="/products/:ean" element={<Product />} />
				<Route element={<ProtectedRoute />}>
					<Route path="/tickets" element={<Tickets />} />
					<Route path="/tickets/search" element={<SearchTicket />} />
					<Route path="/tickets/:ticketId" element={<Ticket />} />
					<Route element={<RoleRoute roles={["Customer", "Expert"]} />}>
						<Route path="/me" element={<Profile />} />
					</Route>
					<Route element={<RoleRoute roles={["Customer"]} />}>
						<Route path="/tickets/new" element={<CreateTicket />} />
						<Route path="/me/edit" element={<EditProfile />} />
					</Route>
					<Route element={<RoleRoute roles={["Manager"]} />}>
						<Route path="/manager/experts" element={<Experts />}/>
						<Route path="/manager/create-expert" element={<CreateExpert />} />
						<Route path="/manager/changes" element={<Changes />} />
					</Route>
				</Route>
			</Route>
			<Route path="*" element={<Error />} />
		</Routes>
	);
}

export default App;
