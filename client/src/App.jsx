// Imports
import {Route, Routes, useLocation} from "react-router-dom";

// Views
import * as Views from "@views";

// Components
import {Layout} from "@components/layout";
import {GuestRoute, ProtectedRoute, RoleRoute} from "@components/security";

function App() {
    const location = useLocation();

    return (
        <Routes location={location} key={location.pathname}>
            <Route element={<Layout/>}>
                <Route index path="/" element={<Views.Home/>}/>

                <Route path="/products" element={<Views.Products/>}/>
                <Route path="/products/search" element={<Views.SearchProduct/>}/>
                <Route path="/products/:ean" element={<Views.Product/>}/>

                <Route element={<GuestRoute/>}>
                    <Route path="/login" element={<Views.Login/>}/>
                    <Route path="/signup" element={<Views.Signup/>}/>
                </Route>

                <Route element={<ProtectedRoute/>}>
                    <Route path="/tickets" element={<Views.Tickets/>}/>
                    <Route path="/tickets/search" element={<Views.SearchTicket/>}/>
                    <Route path="/tickets/:ticketId" element={<Views.Ticket/>}/>

                    <Route element={<RoleRoute roles={["Customer", "Expert"]}/>}>
                        <Route path="/tickets/:ticketId/chat" element={<Views.Chat/>}/>
                        <Route path="/me" element={<Views.Profile/>}/>
                    </Route>

                    <Route element={<RoleRoute roles={["Customer"]}/>}>
                        <Route path="/tickets/new" element={<Views.CreateTicket/>}/>
                        <Route path="/me/edit" element={<Views.EditProfile/>}/>
                        <Route path="/me/purchases" element={<Views.PurchasedProducts/>}/>
                        <Route path="/me/purchases/register" element={<Views.RegisterPurchase/>}/>
                    </Route>

                    <Route element={<RoleRoute roles={["Manager"]}/>}>
                        <Route path="/manager/experts" element={<Views.Experts/>}/>
                        <Route path="/manager/experts/new" element={<Views.CreateExpert/>}/>
                        <Route path="/manager/changes" element={<Views.Changes/>}/>
                    </Route>
                </Route>
            </Route>

            <Route path="*" element={<Views.Error/>}/>
        </Routes>
    );
}

export default App;
