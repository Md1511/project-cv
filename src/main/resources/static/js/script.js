// $(function (){
//     var $userRegister=$("#userRegister");
//
//     $userRegister.validate({
//
//         rules:{
//             name:{
//                 required: true,
//                 lettersonly:true
//             },
//             email:{
//                 required: 'email name must be required',
//                 space: 'space not allowed',
//                 email: 'Invalid email'
//             },
//             mobileNumber: {
//                 required: 'mob no must be required',
//                 space: 'space not allowed',
//                 numericOnly: 'Invalid mob no',
//                 minlength: 'min 10 digits',
//                 maxlength: 'max 11 digits',
//             },
//             password: {
//                 required: 'password must be required',
//                 space: 'space not allowed'
//             },
//             confirmpassword: {
//                 required: 'confirmpassword must be required',
//                 space: 'space not allowed',
//                 equaTo: 'password mismatch'
//             },
//             address: {
//                 required: 'address must be required',
//                 all: 'invalid'
//             },
//             city: {
//                 required: 'city must be required',
//                 space: 'space not allowed'
//             },
//             state: {
//                 required: 'state must be required',
//                 space: 'space not allowed'
//             },
//             pincode: {
//                 required: true,
//                 space: true,
//                 numericOnly: true
//             },
//             img: {
//                 required: true
//             }
//         },
//         messages:{
//             name:{
//                 required: 'Name required',
//                 lettersonly:'invalid name'
//             }
//         }
//     })
// })
//
// jQuery.validator.addMethod('lettersonly', function(value, element) {
//     return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
// });
//
// jQuery.validator.addMethod('space', function(value, element) {
//     return /^[^-\s]+$/.test(value);
// });
//
// jQuery.validator.addMethod('all', function(value, element) {
//     return /^[^-\s][a-zA-Z0-9_\s-]+$/.test(value);
// });
//
// jQuery.validator.addMethod('numericOnly', function(value, element) {
//     return /^[0-9]+$/.test(value);
// });
//
//
