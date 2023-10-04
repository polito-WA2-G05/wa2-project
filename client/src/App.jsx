import {Routes, Route, useLocation} from "react-router-dom";
import {Home, Products, ProductItem, SearchProfile, Profile, UpdateProfile, AddProfile, Login} from './views'
import {Layout} from "./components";

function App() {
    const location = useLocation();

    return (
        <Routes location={location} key={location.pathname}>
            <Route element={<Layout/>}>
                <Route index path='/' element={<Home/>}/>
                <Route path='/login' element={<Login/>}/>
                <Route path='/products' element={<Products/>}/>
                <Route path='/products/:ean' element={<ProductItem/>}/>
                {/*
                <Route path='/profiles' element={<SearchProfile/>}/>
                <Route path='/profiles/:email' element={<Profile/>}/>
                <Route path='/profiles/:email/update' element={<UpdateProfile/>}/>
                <Route path='/profiles/add' element={<AddProfile/>}/>
                */}
            </Route>
        </Routes>
    );
}

export default App;
