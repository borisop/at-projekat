const Homepage = { template: '<homepage></homepage>' }
const SendMessage = { template: '<sendMessage></sendMessage>' }

const router = new VueRouter({
	mode: 'hash',
	routes: [
		{ path: '/', component: Homepage },
		{ path: '/message/send', component: SendMessage }
	]
});

var app = new Vue({
	router,
	el: '#app',
	data: {
		
	},
	mounted: function() {
		router.push('/');
	},
	methods: {
		
	}
});
